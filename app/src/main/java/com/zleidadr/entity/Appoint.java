package com.zleidadr.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xiaoxuli on 16/1/7.
 */
public class Appoint extends SugarRecord {

    @Unique
    private String projectAppointId;
    private String loanee_business_city;
    private String statusName;
    private String appointEndDate;
    private String projectAddressId;
    private String projectId;
    private String visitTimes;
    private String statusNum;
    private String authorize_amount;

    public Appoint() {
    }

    public String getProjectAppointId() {
        return projectAppointId;
    }

    public void setProjectAppointId(String projectAppointId) {
        this.projectAppointId = projectAppointId;
    }

    public String getLoanee_business_city() {
        return loanee_business_city;
    }

    public void setLoanee_business_city(String loanee_business_city) {
        this.loanee_business_city = loanee_business_city;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getAppointEndDate() {
        return appointEndDate;
    }

    public void setAppointEndDate(String appointEndDate) {
        this.appointEndDate = appointEndDate;
    }

    public String getProjectAddressId() {
        return projectAddressId;
    }

    public void setProjectAddressId(String projectAddressId) {
        this.projectAddressId = projectAddressId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getVisitTimes() {
        return visitTimes;
    }

    public void setVisitTimes(String visitTimes) {
        this.visitTimes = visitTimes;
    }

    public String getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(String statusNum) {
        this.statusNum = statusNum;
    }

    public String getAuthorize_amount() {
        return authorize_amount;
    }

    public void setAuthorize_amount(String authorize_amount) {
        this.authorize_amount = authorize_amount;
    }
}
