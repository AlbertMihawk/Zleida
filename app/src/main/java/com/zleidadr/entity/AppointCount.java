package com.zleidadr.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xiaoxuli on 16/1/7.
 */
public class AppointCount extends SugarRecord {

    private String count;
    @Unique
    private String statusNum;

    public AppointCount() {
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(String statusNum) {
        this.statusNum = statusNum;
    }

}
