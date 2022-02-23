package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TrailMapping {

    public static String COLLECTION_NAME = "core.Trail";

    public static String ID = Trail.ID;
    public static String NAME = Trail.NAME;
    public static String CODE = Trail.CODE;

    private String id;
    private String name;
    private String code;
}
