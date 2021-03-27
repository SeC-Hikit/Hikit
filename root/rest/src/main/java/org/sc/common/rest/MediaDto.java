package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
public class MediaDto {
    private Date creationDate;
    private String id;
    private String name;
    private String fileName;
    private String fileUrl;
    private String mime;
    private long fileSize;
}
