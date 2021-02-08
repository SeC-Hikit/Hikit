package org.sc.data.entity;

public class KeyVal {

    public final static String KEY = "key";
    public final static String VAL = "val";

    private String key;
    private String val;

    public KeyVal(String key, String val) {
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
