package org.sc.common.rest.controller;

import java.util.Collections;
import java.util.Set;

public class FileDownloadResponse extends RESTResponse {
    private final String path;

    public FileDownloadResponse(String path) {
        super(Status.OK, Collections.emptySet());
        this.path = path;
    }

    public FileDownloadResponse(String path, Status error, Set<String> errors) {
        super(error, errors);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
