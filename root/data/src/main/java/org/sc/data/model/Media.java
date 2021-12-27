package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class Media {

    public static final String COLLECTION_NAME = "core.Media";

    public static final String OBJECT_ID = "_id";
    public static final String CREATION_DATE = "creationDate";
    public static final String NAME = "name";
    public static final String FILENAME = "fileName";
    public static final String FILE_URL = "fileUrl";
    public static final String MIME = "mime";
    public static final String FILE_SIZE = "fileSize";
    public static final String RECORD_DETAILS = "recordDetails";
    public static final String IS_COMPRESSED = "isCompressed";
    public static final String RESOLUTIONS = "resolutions";

    private String id;
    private Date creationDate;
    private String name;
    private String fileName;
    private String fileUrl;
    private String mime;
    private Long fileSize;
    private FileDetails fileDetails;
    private boolean isCompressed;
    private List<String> resolutions;

}
