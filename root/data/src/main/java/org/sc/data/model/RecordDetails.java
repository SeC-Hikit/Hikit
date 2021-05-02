package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDetails {

    public static final String UPLOADED_ON = "uploadedOn";
    public static final String UPLOADED_BY = "uploadedBy";
    public static final String ON_INSTANCE = "onInstance";
    public static final String REALM = "realm";

    private Date uploadedOn;
    private String uploadedBy;
    private String onInstance;
    private String realm;
}
