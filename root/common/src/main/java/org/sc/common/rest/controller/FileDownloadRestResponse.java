package org.sc.common.rest.controller;

import java.util.Collections;
import java.util.Set;

public class FileDownloadRestResponse extends RESTResponse {
    private final String path;

    public FileDownloadRestResponse(String path) {
        super(Status.OK, Collections.emptySet());
        this.path = path;
    }

    public <T> FileDownloadRestResponse(String path, Status error, Set<String> errors) {
        super(error, errors);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
