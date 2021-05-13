package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TrailProvider {
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PARENT_ID = "parentId";
    public static final String DESCRIPTION = "description";
    public static final String PUBLIC_PRIVATE = "isPublic";
    public static final String KEY_VAL = "kvps";

    private String id;
    private String name;
    private String parentId;
    private String description;
    private boolean publicOrganization;
    private List<KeyVal> keyVal;
}
