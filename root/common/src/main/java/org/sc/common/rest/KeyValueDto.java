package org.sc.common.rest;

public class KeyValueDto {
    private String key;
    private String val;

    public KeyValueDto(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
