package org.sc.data.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Instance {

    public static final String COLLECTION_NAME = "core.Instance";

    public static String ID = "_id";
    public static String INSTANCE_ID = "instanceId";
    public static String NAME = "name";
    public static String HOSTNAME = "hostname";
    public static String BOOT_TIME = "bootTime";

    private String id;
    private String instanceId;
    private String nameId;
    private String hostname;
    private String bootTime;
}
