package com.zleidadr.api;

/**
 * Created by xiaoxuli on 16/1/5.
 */
public class ApiRequest<T> {

    private String method;
    private String ver;
    private String token;
    private T params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}
