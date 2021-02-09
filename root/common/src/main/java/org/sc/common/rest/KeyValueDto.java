package org.sc.common.rest;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyValueDto that = (KeyValueDto) o;
        return getKey().equals(that.getKey()) && getVal().equals(that.getVal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getVal());
    }
}
