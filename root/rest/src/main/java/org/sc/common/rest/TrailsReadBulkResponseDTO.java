package org.sc.common.rest;

import java.util.List;

public class TrailsReadBulkResponseDTO {

    private List<TrailPreparationModelDto> trailsResult;
    private List<String> filesNameWithError;


    public List<String> getFilesNameWithError() {
        return filesNameWithError;
    }

    public void setFilesNameWithError(List<String> filesNameWithError) {
        this.filesNameWithError = filesNameWithError;
    }

    public List<TrailPreparationModelDto> getTrailsResult() {
        return trailsResult;
    }

    public void setTrailsResult(List<TrailPreparationModelDto> trailsResult) {
        this.trailsResult = trailsResult;
    }
}
