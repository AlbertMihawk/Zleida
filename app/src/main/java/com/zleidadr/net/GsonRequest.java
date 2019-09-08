package com.zleidadr.net;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zleidadr.api.ApiRequest;
import com.zleidadr.common.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.UnsupportedEncodingException;

/**
 * Created by xiaoxuli on 16/1/6.
 */
public class GsonRequest<T> extends JsonRequest<T> {
    private static final String TAG = GsonRequest.class.getName();

    private final Class<T> clazz;
    private final TypeToken<T> typeToken;
    private HttpEntity mHttpEntity;
    private MultipartEntityBuilder mMultipartEntityBuilder = MultipartEntityBuilder.create();

    public GsonRequest(int method, String url, ApiRequest apiRequest, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, new Gson().toJson(apiRequest), listener, errorListener);
        this.clazz = clazz;
        this.typeToken = null;
    }

    public GsonRequest(int method, String url, ApiRequest apiRequest, TypeToken<T> typeToken, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, new Gson().toJson(apiRequest), listener, errorListener);
        this.clazz = null;
        this.typeToken = typeToken;
    }

    public GsonRequest(String url, ApiRequest apiRequest, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.POST, url, apiRequest, clazz, listener, errorListener);
    }

    public GsonRequest(String url, ApiRequest apiRequest, TypeToken<T> typeToken, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.POST, url, apiRequest, typeToken, listener, errorListener);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Logger.d(TAG, "get url:" + this.getUrl() + " , get Data:" + json);
            Gson gson = new Gson();
            if (this.clazz != null) {
                return Response.success(
                        gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
            }
            return Response.success(
                    (T) gson.fromJson(json, typeToken.getType()),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    public HttpEntity getHttpEntity() {

        if (null == mHttpEntity) mHttpEntity = mMultipartEntityBuilder.build();
        return mHttpEntity;
    }

    public MultipartEntityBuilder getmMultipartEntityBuilder() {
        return mMultipartEntityBuilder;
    }

    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {

        return super.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
