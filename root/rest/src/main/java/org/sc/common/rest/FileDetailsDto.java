package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class FileDetailsDto {
    private Date uploadedOn;
    private String uploadedBy;
    private String filename;
    private String originalFilename;
}
