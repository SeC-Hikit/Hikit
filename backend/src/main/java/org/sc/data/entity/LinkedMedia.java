package org.sc.data.entity;

import java.util.List;

public class LinkedMedia {

    public static String ID = "id";
    public static String DESCRIPTION = "description";
    public static final String KEY_VAL = "kvps";

    private String id;
    private String description;
    private List<KeyVal> keyValList;

    public LinkedMedia(String id,
                       String description,
                       List<KeyVal> keyValList) {
        this.id = id;
        this.description = description;
        this.keyValList = keyValList;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<KeyVal> getKeyValList() {
        return keyValList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKeyValList(List<KeyVal> keyValList) {
        this.keyValList = keyValList;
    }
}
