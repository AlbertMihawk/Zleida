package com.zleidadr.entity;

/**
 * Created by xiaoxuli on 16/1/6.
 */
public class Operator {
    private String operatorId;
    private String operatorName;
    private String status;
    private String token;
    private String tokenLastTime;
    private String addedTime;

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTokenLastTime() {
        return tokenLastTime;
    }

    public void setTokenLastTime(String tokenLastTime) {
        this.tokenLastTime = tokenLastTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }
}
