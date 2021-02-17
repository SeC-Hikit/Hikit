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
    private String fileSize;

    public MediaDto(String id, Date creationDate, String name,
                    String fileName, String fileUrl,
                    String mime, String fileSize) {
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

    public String getFileSize() {
        return fileSize;
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

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaDto that = (MediaDto) o;
        return getCreationDate().equals(that.getCreationDate()) && getId().equals(that.getId()) && getName().equals(that.getName()) && getFileName().equals(that.getFileName()) && getFileUrl().equals(that.getFileUrl()) && getMime().equals(that.getMime()) && getFileSize().equals(that.getFileSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreationDate(), getId(), getName(), getFileName(), getFileUrl(), getMime(), getFileSize());
    }
}
