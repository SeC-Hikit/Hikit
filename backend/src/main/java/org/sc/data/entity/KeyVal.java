package org.sc.data.entity;

public class KeyVal {

    public final static String KEY = "key";
    public final static String VAL = "val";

    private String key;
    private String value;

    public KeyVal(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
