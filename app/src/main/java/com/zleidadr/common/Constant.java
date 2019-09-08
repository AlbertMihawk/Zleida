package com.zleidadr.common;

/**
 * Created by xiaoxuli on 16/1/15.
 */
public class Constant {

    public static final String APPOINT_STATUS_AWAIT = "1";//待外访
    public static final String APPOINT_STATUS_DONE = "2";//在催
    public static final String APPOINT_STATUS_SUCCESS = "3";//催收成功
    public static final String APPOINT_STATUS_OVERDUE = "4";//催收过期

    public static final String RECEIVABLE_STATUS_CHECK = "10";
    public static final String RECEIVABLE_STATUS_REFUSE = "20";
    public static final String RECEIVABLE_STATUS_PASS = "30";

    public static final String SEX_MALE = "1";

    public static final String SEX_FEMALE = "2";
    public static final String SEX_MALE_STR = "男";
    public static final String SEX_FEMALE_STR = "女";
    public static final String RESOURCE_PHOTO = "10";

    public static final String RESOURCE_AUDIO = "20";
    public static final String DICTKEY_SELF = "1";// 本人


    public static final String DICTNAME_ACCESS_OBJECT = "ACCESS_OBJECT"; // 获取外访对象
    public static final String DICTNAME_ACCESS_RESULT_SELF = "ACCESS_RESULT_SELF";//获取外访结果 本人
    public static final String DICTNAME_ACCESS_RESULT_OTHER = "ACCESS_RESULT_OTHER";//获取外访结果 非本人
    public static final String DICTNAME_ADDRESS_VALIDITY = "ADDRESS_VALIDITY"; //获取地址有效性

    public static final int STATE_INIT = -1;//草稿箱
    public static final int STATE_DRAFT = 0;//草稿箱
    public static final int STATE_STAY = 1;//待提交箱子
    public static final int STATE_END = 2;//已提交(隐藏数据)


    public static final String BUNDLE_STR_PROJECTID = "projectId";
    public static final String BUNDLE_STR_LOCALID = "localId";


}
