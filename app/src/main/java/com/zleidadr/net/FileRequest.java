package com.zleidadr.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by xiaoxuli on 2017/1/13.
 */

public class FileRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;

    private static final Object sDecodeLock = new Object();

    public FileRequest(String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mListener = listener;
    }


    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        // Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                if (response.data == null) {
                    return Response.error(new ParseError(response));
                } else {
                    return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
                }
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }


    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }


}
