package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class FileDetails {

    public static final String UPLOADED_ON = "uploadedOn";
    public static final String UPLOADED_BY = "uploadedBy";
    public static final String ON_INSTANCE = "onInstance";
    public static final String REALM = "realm";
    public static final String FILENAME = "filename";
    public static final String ORIGINAL_FILENAME = "originalFilename";

    private Date uploadedOn;
    private String uploadedBy;
    private String onInstance;
    private String realm;
    private String filename;
    private String originalFilename;
}
