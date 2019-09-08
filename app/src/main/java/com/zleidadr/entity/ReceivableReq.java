package com.zleidadr.entity;

/**
 * Created by xiaoxuli on 16/1/8.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class ReceivableReq extends SugarRecord implements Serializable {

    /**
     * {@link com.zleidadr.common.Constant}
     * STATE_DRAFT = 0;//草稿箱 STATE_STAY = 1;//待提交箱子 STATE_END = 2;//已提交(隐藏数据)
     */

    private int state;

    /**
     * 固定传0
     */
    @Expose
    @SerializedName("bid_id")
    private int bidId;

    @Expose
    @SerializedName("project_id")
    private int projectId;

    @Expose
    @SerializedName("project_code")
    private String projectCode;

    /**
     * 固定传5：上门催收
     */
    @Expose
    @SerializedName("receive_type")
    private int receiveType;

    @Expose
    private String address;
    @Expose
    private String detail;
    /**
     * (1 本人, 2 夫妻, 3 父母, 4 朋友, 5 亲戚, 6 同事, 7 邻居, 8 无关人员
     */
    @Expose
    private String accessObject;
    /**
     * ①外访对象是本人的外访结果: 10 承诺处理, 20 争议, 30 再跟进
     * ②外访对象是非本人的外访结果: 50 帮转告, 60 失联, 70 代偿, 80 再跟进
     */
    @Expose
    private String accessResult;
    /**
     * 10 地址错误, 20 地址空关, 30 地址无此人, 40 地址有效
     */
    @Expose
    private String addressValidity;
    @Expose
    private Integer projectAddressId;
    @Expose
    private Integer projectAppointId;
    @Expose
    @SerializedName("receive_time")
    private String receiveTime;

    @Ignore
    @SerializedName("resources")
    @Expose
    private List<Resource> resourcesList;

    /**
     * 以下信息未Json序列化
     */
    private String authorized_agency;
    private String authorize_amount;
    private String overdue_date;
    private String loanee_name;

    @SerializedName("receive_company")
    private String receiveCompany;


    @SerializedName("receive_tel")
    private String receiveTel;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;
    private String receiver;

    private String auditStatus;

    public ReceivableReq() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getBidId() {
        return bidId;
    }

    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public int getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(int receiveType) {
        this.receiveType = receiveType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAccessObject() {
        return accessObject;
    }

    public void setAccessObject(String accessObject) {
        this.accessObject = accessObject;
    }

    public String getAccessResult() {
        return accessResult;
    }

    public void setAccessResult(String accessResult) {
        this.accessResult = accessResult;
    }

    public String getAddressValidity() {
        return addressValidity;
    }

    public void setAddressValidity(String addressValidity) {
        this.addressValidity = addressValidity;
    }

    public Integer getProjectAddressId() {
        return projectAddressId;
    }

    public void setProjectAddressId(Integer projectAddressId) {
        this.projectAddressId = projectAddressId;
    }

    public Integer getProjectAppointId() {
        return projectAppointId;
    }

    public void setProjectAppointId(Integer projectAppointId) {
        this.projectAppointId = projectAppointId;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public List<Resource> getResourcesList() {
        return resourcesList;
    }

    public void setResourcesList(List<Resource> resourcesList) {
        this.resourcesList = resourcesList;
    }


    public String getLoanee_name() {
        return loanee_name;
    }

    public void setLoanee_name(String loanee_name) {
        this.loanee_name = loanee_name;
    }

    public String getOverdue_date() {
        return overdue_date;
    }

    public void setOverdue_date(String overdue_date) {
        this.overdue_date = overdue_date;
    }

    public String getAuthorize_amount() {
        return authorize_amount;
    }

    public void setAuthorize_amount(String authorize_amount) {
        this.authorize_amount = authorize_amount;
    }

    public String getAuthorized_agency() {
        return authorized_agency;
    }

    public void setAuthorized_agency(String authorized_agency) {
        this.authorized_agency = authorized_agency;
    }


}
