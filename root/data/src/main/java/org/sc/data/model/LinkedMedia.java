package org.sc.data.model;

import java.util.List;

public class LinkedMedia {

    public static String ID = "id";
    public static String DESCRIPTION = "description";
    public static final String KEY_VAL = "kvps";

    private String id;
    private String description;
    private List<KeyVal> keyVal;

    public LinkedMedia(String id,
                       String description,
                       List<KeyVal> keyVal) {
        this.id = id;
        this.description = description;
        this.keyVal = keyVal;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<KeyVal> getKeyVal() {
        return keyVal;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKeyVal(List<KeyVal> keyVal) {
        this.keyVal = keyVal;
    }
}
