package org.sc.common.rest;

import java.util.Date;
import java.util.Objects;

public class MediaDto {

    private Date creationDate;
    private String id;
    private String name;
    private String fileName;
    private String fileUrl;
    private String mime;
    private long fileSize;

    public MediaDto(String id, Date creationDate, String name,
                    String fileName, String fileUrl,
                    String mime, long fileSize) {
        this.creationDate = creationDate;
        this.id = id;
        this.name = name;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.mime = mime;
        this.fileSize = fileSize;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getMime() {
        return mime;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaDto mediaDto = (MediaDto) o;
        return getFileSize() == mediaDto.getFileSize() && getCreationDate().equals(mediaDto.getCreationDate()) && getId().equals(mediaDto.getId()) && getName().equals(mediaDto.getName()) && getFileName().equals(mediaDto.getFileName()) && getFileUrl().equals(mediaDto.getFileUrl()) && getMime().equals(mediaDto.getMime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreationDate(), getId(), getName(), getFileName(), getFileUrl(), getMime(), getFileSize());
    }
}
