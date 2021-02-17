package org.sc.data.entity;

import java.util.Date;

public class Media {

    public static final String COLLECTION_NAME = "core.Media";

    public static final String OBJECT_ID = "_id";
    public static final String CREATION_DATE = "creationDate";
    public static final String NAME = "name";
    public static final String FILENAME = "fileName";
    public static final String FILE_URL = "fileUrl";
    public static final String MIME = "mime";
    public static final String FILE_SIZE = "fileSize";

    private String _id;
    private Date creationDate;
    private String name;
    private String fileName;
    private String fileUrl;
    private String mime;
    private String fileSize;

    public Media() { }

    public Media(String _id, Date creationDate, String name,
                 String fileName, String fileUrl, String mime,
                 String fileSize) {
        this._id = _id;
        this.creationDate = creationDate;
        this.name = name;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.mime = mime;
        this.fileSize = fileSize;
    }

    public String get_id() {
        return _id;
    }

    public Date getCreationDate() {
        return creationDate;
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
}
