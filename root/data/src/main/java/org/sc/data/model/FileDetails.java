package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class FileDetails {
    private Date uploadedOn;
    private String uploadedBy;
    private String filename;
}
