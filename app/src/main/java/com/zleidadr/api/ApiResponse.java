package com.zleidadr.api;

import java.util.HashMap;

/**
 * Created by xiaoxuli on 16/1/5.
 */
public class ApiResponse<T> {
    private String method;
    private String ver;
    private String token;
    private String resultCode;
    private String resultMsg;
    private HashMap<String, T> params;

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, T> getParams() {
        return params;
    }

    public void setParams(HashMap<String, T> params) {
        this.params = params;
    }
}
