package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDetailsDto {
    private Date uploadedOn;
    private String uploadedBy;
    private String onInstance;
    private String realm;
    private String filename;
    private String originalFilename;
}
