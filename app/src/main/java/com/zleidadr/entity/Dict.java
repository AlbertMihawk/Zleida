package com.zleidadr.entity;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xiaoxuli on 16/1/7.
 */
public class Dict extends SugarRecord {
    @Unique
    private String dictId;
    private String dictValue;
    private String dictKey;
    private String dictName;

    public Dict() {
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}
