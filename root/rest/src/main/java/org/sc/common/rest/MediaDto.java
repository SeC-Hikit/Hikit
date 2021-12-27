package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class MediaDto {
    private Date creationDate;
    private String id;
    private String name;
    private String fileName;
    private String fileUrl;
    private List<String> resolutionSuffixes;
    private String mime;
    private Long fileSize;
    private FileDetailsDto fileDetails;
}
