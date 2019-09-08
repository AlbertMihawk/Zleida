package com.zleidadr.entity;

/**
 * Created by xiaoxuli on 2017/1/12.
 */

public class Banner {
    private String img;
    private String accessURL;
    private String templateCode;
    private String templateId;
    private String baseURL;
    private String templateName;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
