package com.zleidadr.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xiaoxuli on 16/1/7.
 */
public class Address extends SugarRecord {
    @Unique
    private String projectAddressId;
    private String addressType;
    private String visitTimes;
    private String address;

    public Address() {
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getVisitTimes() {
        return visitTimes;
    }

    public void setVisitTimes(String visitTimes) {
        this.visitTimes = visitTimes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProjectAddressId() {
        return projectAddressId;
    }

    public void setProjectAddressId(String projectAddressId) {
        this.projectAddressId = projectAddressId;
    }
}
