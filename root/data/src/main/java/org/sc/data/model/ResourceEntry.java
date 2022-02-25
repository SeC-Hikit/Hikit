package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEntry {

    public static final String COLLECTION_NAME = "core.job.resource";

    public static final String OBJECT_ID = "_id";
    public static final String INSTANCE_ID = "instanceId";
    public static final String ENTRY_TYPE = "entryType";
    public static final String ENTRY_ID = "entryId";
    public static final String ACTION = "action";
    public static final String TARGETING_TRAIL = "targetingTrailId";
    public static final String CREATED_ON = "createdOn";
    public static final String USER_PROMPTING = "userPrompting";

    private String id;
    private String instanceId;
    private String entryType;
    private String entryId;
    private String targetingTrail;
    private String action;
    private Date createdOn;
    private String userPrompting;
}
