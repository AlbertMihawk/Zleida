package com.zleidadr.entity;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;


public class Resource extends SugarRecord implements Serializable {


    private String localId;
    @Expose
    private int resourceId;
    @Expose
    private String location;
    @Expose
    private String longitude;
    @Expose
    private String latitude;
    @Expose
    private String province;
    @Expose
    private String city;
    @Expose
    private String district;
    @Expose
    private String street;
    @Expose
    private String resourceType; //10: 图片  20：录音 30:视频
    @Expose
    private String resourceOriginal;
//    @Expose
//    private String resourceThumbnail;

//    @Expose
//    private ReceivableReq receivableReq;

//    public ReceivableReq getReceivableReq() {
//        return receivableReq;
//    }
//
//    public void setReceivableReq(ReceivableReq receivableReq) {
//        this.receivableReq = receivableReq;
//    }

    /**
     * 以下信息未使用
     */
    private int resourceServiceId;
    private String resourceServiceType;
    private Integer resourceSeq;
    private String resourceName;
    private String resourcePath;
    private Integer fileSize;
    private String status;
    private String business;
    private Date addedTime;

    public Resource() {
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceOriginal() {
        return resourceOriginal;
    }

    public void setResourceOriginal(String resourceOriginal) {
        this.resourceOriginal = resourceOriginal;
    }

//    public String getResourceThumbnail() {
//        return resourceThumbnail;
//    }
//
//    public void setResourceThumbnail(String resourceThumbnail) {
//        this.resourceThumbnail = resourceThumbnail;
//    }
}