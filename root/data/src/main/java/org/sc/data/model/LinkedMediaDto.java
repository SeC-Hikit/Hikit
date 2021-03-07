package org.sc.common.rest;

import java.util.List;
import java.util.Objects;

public class LinkedMediaDto {
    private String id;
    private String description;
    private List<KeyValueDto> keyVal;

    public LinkedMediaDto() {
    }

    public LinkedMediaDto(String id,
                          String description,
                          List<KeyValueDto> keyVal) {
        this.id = id;
        this.description = description;
        this.keyVal = keyVal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<KeyValueDto> getKeyVal() {
        return keyVal;
    }

    public void setKeyVal(List<KeyValueDto> keyVal) {
        this.keyVal = keyVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedMediaDto that = (LinkedMediaDto) o;
        return getId().equals(that.getId()) &&
                getDescription().equals(that.getDescription()) &&
                getKeyVal().equals(that.getKeyVal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDescription(), getKeyVal());
    }
}
