package com.zleidadr.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xiaoxuli on 16/1/7.
 */
public class Receivable extends SugarRecord {

    @Unique
    private String receivableId;

    private String auditStatus;
    private String receive_time;
    private String project_code;
    private String address;
    private String auditStatusName;
    private String refuseReason;

    public Receivable() {
    }

    public String getReceivableId() {
        return receivableId;
    }

    public void setReceivableId(String receivableId) {
        this.receivableId = receivableId;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuditStatusName() {
        return auditStatusName;
    }

    public void setAuditStatusName(String auditStatusName) {
        this.auditStatusName = auditStatusName;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }
}
