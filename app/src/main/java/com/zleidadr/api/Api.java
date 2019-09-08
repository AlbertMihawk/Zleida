package com.zleidadr.api;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zleidadr.common.Constant;
import com.zleidadr.common.Logger;
import com.zleidadr.entity.Address;
import com.zleidadr.entity.Appoint;
import com.zleidadr.entity.AppointCount;
import com.zleidadr.entity.AppointDetail;
import com.zleidadr.entity.Banner;
import com.zleidadr.entity.Dict;
import com.zleidadr.entity.Operator;
import com.zleidadr.entity.Receivable;
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.manager.NetworkManager;
import com.zleidadr.net.GsonRequest;
import com.zleidadr.net.MySSLSocketFactory;
import com.zleidadr.sugarDb.DbManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaoxuli on 16/1/5.
 */
public class Api {

    //    public static final String HOST = "http://192.168.207.55:8087";
//    public static final String HOST = "http://zleida-app.qa-01.jimubox.com";
        public static final String HOST = "https://app.zleida.com";
    private static final String TAG = Api.class.getName();
    private static final String CODE_SUCCESS = "1";
    private static final String CODE_FAIL = "2";
    private static final String VERSION = "1.0";
    private static Api mApi;
    private Context mCtx;
    private RequestQueue mRequestQueue;

    private Api(Context context) {
        mCtx = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized void init(Context context) {
        if (mApi == null) {
            mApi = new Api(context);
        }
    }

    /**
     * pls run init(context) method in application initial.
     *
     * @return mApi
     */
    public static Api getInstance() {
        return mApi;
    }


    public static String getImageUrl(String picFilePath) {
        return HOST + "/getFileFlow?filePath=" + picFilePath;
    }

    public static String getImageUrl(String host, String picFilePath) {
        return host + picFilePath;
    }

    private boolean checkNetwork(Callback callback) {
        if (!NetworkManager.getInstance().isNetworkConnected(mCtx)) {
            callback.onApiFailed(CODE_FAIL, NetworkManager.NETWORK_ERROR_MSG);
            return false;
        }
        return true;
    }

    private boolean checkNetworkStatus() {
        return NetworkManager.getInstance().isNetworkConnected(mCtx);
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @param callback
     */
    public void login(String userName, String password, final Callback<Operator> callback) {
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("login");
        apiRequest.setVer(VERSION);
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("password", password);
        apiRequest.setParams(map);

        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<Operator>>() {
        }, new Response.Listener<ApiResponse<Operator>>() {
            @Override
            public void onResponse(ApiResponse<Operator> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    callback.onApiSuccess(response.getParams().get("operator"));
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }

            }
        }, new DefaultErrorLitener(callback));

        mRequestQueue.add(req);
    }

    public void getBannerApi(final Callback<ArrayList<Banner>> callback) {
        if (!checkNetworkStatus()) {
            return;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getBanners");
        apiRequest.setVer(VERSION);
        Map<String, String> map = new HashMap<>();
        apiRequest.setParams(map);
        Request<ApiResponse<ArrayList<Banner>>> req = new GsonRequest<>(HOST + "/execute", apiRequest, new TypeToken<ApiResponse<ArrayList<Banner>>>() {
        }, new Response.Listener<ApiResponse<ArrayList<Banner>>>() {
            @Override
            public void onResponse(ApiResponse<ArrayList<Banner>> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    ArrayList<Banner> banners = response.getParams().get("banners");
                    callback.onApiSuccess(banners);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));

        mRequestQueue.add(req);
    }

    /**
     * 主页
     *
     * @param operatorId
     * @param callback
     */
    public void getProjectAppointCountApi(final String operatorId, final Callback<List<AppointCount>> callback) {
        if (!checkNetworkStatus()) {
            List<AppointCount> list = DbManager.getProjectAppointCountDb();
            if (list != null) {
                callback.onApiSuccess(list);
            } else {
                callback.onApiFailed("404", "数据库读取异常");
            }
            return;
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getProjectAppointCount");
        apiRequest.setVer(VERSION);
        Map<String, String> map = new HashMap<>();
        map.put("operatorId", operatorId);
        apiRequest.setParams(map);

        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<List<AppointCount>>>() {
        }, new Response.Listener<ApiResponse<List<AppointCount>>>() {
            @Override
            public void onResponse(ApiResponse<List<AppointCount>> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    List<AppointCount> list = response.getParams().get("counts");
                    DbManager.saveProjectAppointCountDb(list);
                    callback.onApiSuccess(list);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));

        mRequestQueue.add(req);
    }

    /**
     * 外催单
     *
     * @param projectCode
     * @param operatorId
     * @param statusNum
     * @param address
     * @param page
     * @param callback
     */
    public void getProjectAppointListApi(String operatorId, String projectCode, String statusNum, String address, final int page, final Callback<List<Appoint>> callback) {
        if (!checkNetworkStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Appoint> list = DbManager.getProjectAppointListDb();
                    if (list != null) {
                        callback.onApiSuccess(list);
                    } else {
                        callback.onApiFailed("404", "数据库读取异常");
                    }
                }
            }).start();
            return;
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getProjectAppointList");
        apiRequest.setVer(VERSION);
        apiRequest.setToken("");
        Map<String, String> map = new HashMap<>();
        map.put("projectCode", projectCode);
        map.put("operatorId", operatorId);
        map.put("statusNum", statusNum);
        map.put("address", address);
        map.put("page", page + "");
        apiRequest.setParams(map);

        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<List<Appoint>>>() {
        }, new Response.Listener<ApiResponse<List<Appoint>>>() {

            @Override
            public void onResponse(ApiResponse<List<Appoint>> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    List<Appoint> list = response.getParams().get("appointList");
                    DbManager.saveProjectAppointListDb(list);
                    callback.onApiSuccess(list);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));


        mRequestQueue.add(req);
    }

    /**
     * 催单详情
     *
     * @param operatorId
     * @param projectId
     * @param callback
     */
    public void getProjectAppointDetailApi(String operatorId, String projectId, final Callback<AppointDetail> callback) {
        if (!checkNetworkStatus()) {
            AppointDetail detail = DbManager.getProjectAppointDetailDb(projectId);
            if (detail != null) {
                callback.onApiSuccess(detail);
            } else {
                callback.onApiFailed("404", "数据库读取异常");
            }
            return;
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getProjectAppointDetail");
        apiRequest.setVer(VERSION);
        apiRequest.setToken("");
        Map<String, String> map = new HashMap<>();
        map.put("operatorId", operatorId);
        map.put("projectId", projectId);
        apiRequest.setParams(map);
        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<AppointDetail>>() {
        }, new Response.Listener<ApiResponse<AppointDetail>>() {

            @Override
            public void onResponse(ApiResponse<AppointDetail> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    AppointDetail detail = response.getParams().get("appointDetails");
                    DbManager.saveProjectAppointDetailDb(detail);
                    callback.onApiSuccess(detail);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));
        mRequestQueue.add(req);
    }

    /**
     * 外访地址列表
     *
     * @param projectId
     * @param callback
     */
    public void getProjectAddressListApi(String projectId, final Callback<List<Address>> callback) {
        if (!checkNetworkStatus()) {
            List<Address> list = DbManager.getProjectAddressListDb();
            if (list != null) {
                callback.onApiSuccess(list);
            } else {
                callback.onApiFailed("404", "数据库读取异常");
            }
            return;
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getProjectAddressList");
        apiRequest.setVer(VERSION);
        apiRequest.setToken("");
        Map<String, String> map = new HashMap<>();
        map.put("projectId", projectId);
        apiRequest.setParams(map);
        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<List<Address>>>() {
        }, new Response.Listener<ApiResponse<List<Address>>>() {

            @Override
            public void onResponse(ApiResponse<List<Address>> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    List<Address> list = response.getParams().get("projectAddressList");
                    DbManager.saveProjectAddressListDb(list);
                    callback.onApiSuccess(list);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));
        mRequestQueue.add(req);
    }

    /**
     * 字典接口
     * map.put("dictName", "ACCESS_OBJECT,ADDRESS_VALIDITY,ACCESS_RESULT_SELF,ACCESS_RESULT_OTHER");//参数的逗号两边不要加空格
     *
     * @param callback
     */

    public void getDictMapApi(final Callback<Map<String, List<Dict>>> callback) {
        if (!checkNetworkStatus()) {
            Map<String, List<Dict>> map = DbManager.getDictMapDb();
            if (map != null) {
                callback.onApiSuccess(map);
            } else {
                callback.onApiFailed("404", "数据库读取异常");
            }
            return;
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getDictList");
        apiRequest.setVer(VERSION);
        apiRequest.setToken("");
        Map<String, String> map = new HashMap<>();
        map.put(
                "dictNames", Constant.DICTNAME_ACCESS_OBJECT + "," +
                        Constant.DICTNAME_ADDRESS_VALIDITY + "," +
                        Constant.DICTNAME_ACCESS_RESULT_SELF + "," +
                        Constant.DICTNAME_ACCESS_RESULT_OTHER);
        apiRequest.setParams(map);
        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<List<Dict>>>() {
        }, new Response.Listener<ApiResponse<List<Dict>>>() {

            @Override
            public void onResponse(ApiResponse<List<Dict>> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    Map<String, List<Dict>> map = response.getParams();
                    DbManager.saveDictMapDb(map);
                    callback.onApiSuccess(map);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));
        mRequestQueue.add(req);
    }

    public void getReceivableListApi(String operatorId, int page, final Callback<List<Receivable>> callback) {
        if (!checkNetworkStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Receivable> list = DbManager.getReceivableListDb();
                    if (list != null) {
                        callback.onApiSuccess(list);
                    } else {
                        callback.onApiFailed("404", "数据库读取异常");
                    }
                }
            }).start();
            return;
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod("getReceivableList");
        apiRequest.setVer(VERSION);
        apiRequest.setToken("");
        Map<String, String> map = new HashMap<>();
        map.put("operatorId", operatorId);
        map.put("page", page + "");
        apiRequest.setParams(map);
        Request req = new GsonRequest<>(
                HOST + "/execute", apiRequest, new TypeToken<ApiResponse<List<Receivable>>>() {
        }, new Response.Listener<ApiResponse<List<Receivable>>>() {

            @Override
            public void onResponse(ApiResponse<List<Receivable>> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    List<Receivable> list = response.getParams().get("receivableList");
                    DbManager.saveReceivableListDb(list);
                    callback.onApiSuccess(list);
                } else {
                    Logger.e(TAG, "respose error: result -> " + response.getResultCode() + " ResultMsg -> " + response.getResultMsg());
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));
        mRequestQueue.add(req);
    }

    public void downloadFile(String url, final Callback<byte[]> callback) {
        if (!checkNetworkStatus()) {
            return;
        }
        Request<byte[]> req = new Request<byte[]>(Request.Method.GET, url, new DefaultErrorLitener(callback)) {
            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                return null;
            }

            @Override
            protected void deliverResponse(byte[] response) {
                callback.onApiSuccess(response);
            }
        };

        mRequestQueue.add(req);
    }

   /* public void saveReceivableApi3(String operatorId, ReceivableReq receivableReq, File picFile1, File picFile2, File picFile3, File audioFile, final Callback<String> callback) {
        if (!checkNetwork(callback)) {
            return;
        }
        Map map = new HashMap();
        map.put("operatorId", operatorId);
        map.put("receivable", receivableReq);

        String json = new Gson().toJson(map);
        Logger.d(TAG, "saveReceivableApi -> json: " + json);

        GsonRequest req = new GsonRequest<>(
                Request.Method.POST, HOST + "/saveReceivable", null,
                new TypeToken<ApiResponse<String>>() {
                }, new Response.Listener<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> response) {
                if (response.getResultCode().equals(CODE_SUCCESS)) {
                    callback.onApiSuccess("ok");
                } else {
                    callback.onApiFailed(response.getResultCode(), response.getResultMsg());
                }
            }
        }, new DefaultErrorLitener(callback));

        MultipartEntityBuilder meb = req.getmMultipartEntityBuilder();

        meb.addBinaryBody("picFile1", picFile1);
        meb.addBinaryBody("picFile2", picFile2);
        meb.addBinaryBody("picFile3", picFile3);
        meb.addBinaryBody("audioFile", audioFile);
        meb.addTextBody("jsonParams", json);

        mRequestQueue.add(req);
    }*/

    public void saveReceivableApi(String operatorId, ReceivableReq receivableReq, List<File> photoFiles, List<File> audioFiles, final Callback<String> callback) {
        if (!checkNetwork(callback)) {
            return;
        }
        Map map = new HashMap();
        map.put("operatorId", operatorId);
        map.put("receivable", receivableReq);

        String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(map);
        Logger.d(TAG, "saveReceivableApi -> json: " + json);


        BasicHttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", MySSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg);

        final HttpClient httpClient = new DefaultHttpClient(connMgr, params);
        final HttpPost post = new HttpPost(HOST + "/saveReceivable");
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setCharset(Consts.UTF_8);
        meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式

        for (File photoFile : photoFiles) {
            meb.addBinaryBody(photoFile.getName(), photoFile);
        }
        for (File audioFile : audioFiles) {
            meb.addBinaryBody(audioFile.getName(), audioFile);
        }
        meb.addTextBody("jsonParams", json, ContentType.create("text/plain", Consts.UTF_8));

        post.setEntity(meb.build());
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpResponse response;
                            response = httpClient.execute(post);
                            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                HttpEntity entity = response.getEntity();
                                String result = EntityUtils.toString(entity);
                                Gson gson = new Gson();
                                ApiResponse<String> apiResponse = gson.fromJson(
                                        result, new TypeToken<ApiResponse<String>>() {
                                        }.getType());
                                if (apiResponse.getResultCode().equals(CODE_SUCCESS)) {
                                    callback.onApiSuccess("ok");
                                } else {
                                    Logger.e(TAG, "respose error: result -> " + apiResponse.getResultCode() + " ResultMsg -> " + apiResponse.getResultMsg());
                                    callback.onApiFailed(
                                            apiResponse.getResultCode(),
                                            apiResponse.getResultMsg());
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onApiFailed(CODE_FAIL, "网络异常");
                        }
                    }
                }).start();

    }

    public interface Callback<T> {
        public void onApiSuccess(T result);

        public void onApiFailed(String resultCode, String resultMessage);
    }

    private final static class DefaultErrorLitener implements Response.ErrorListener {
        private final Callback callback;

        public DefaultErrorLitener(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse == null) {
                error.printStackTrace();
                callback.onApiFailed(CODE_FAIL, error.getMessage());
            } else {
                callback.onApiFailed(
                        error.networkResponse.statusCode + "",
                        new String(error.networkResponse.data));
            }
        }
    }

    //TODO delete
    /**
     * mApi example
     * public void getXXX(String ~, final Callback<~> callback) {
     * if (!checkNetwork(callback)) {
     * return;
     * }
     * ApiRequest apiRequest = new ApiRequest();
     * apiRequest.setMethod("~");
     * apiRequest.setVer(VERSION);
     * apiRequest.setToken("");
     * Map<String, String> map = new HashMap<>();
     * map.put("~", ~);
     * <p/>
     * Request req = new GsonRequest<>(HOST+"/execute",apiRequest,new TypeToken<ApiResponse<~>>(){},
     * new Response.Listener<ApiResponse<~>() {
     *
     * @Override public void onResponse(ApiResponse<~> response) {
     * if (response.getResultCode().equals(CODE_SUCCESS)) {
     * callback.onApiSuccess(response.getParams().get("~"));
     * }else{
     * callback.onApiFailed(response.getResultCode(),response.getResultMsg());
     * }
     * }
     * }, new DefaultErrorLitener(callback));
     * mRequestQueue.add(req);
     * }
     */
}


