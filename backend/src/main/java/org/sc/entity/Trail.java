package org.sc.entity;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Trail {
    @Id
    private String id;

    private String name;
    private String description;
    private String code;
    private List<String> partOf;
    private String gpx;
    private String geoJson;
    private int length; // in m
    private int heightDifferenceUp;
    private int heightDifferenceDown;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public List<String> getPartOf() {
        return partOf;
    }

    public String getGpx() {
        return gpx;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public int getLength() {
        return length;
    }

    public int getHeightDifferenceUp() {
        return heightDifferenceUp;
    }

    public int getHeightDifferenceDown() {
        return heightDifferenceDown;
    }


}
