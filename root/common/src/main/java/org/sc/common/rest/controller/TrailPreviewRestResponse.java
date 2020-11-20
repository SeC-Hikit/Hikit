package org.sc.common.rest.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TrailPreviewRestResponse extends RESTResponse {
    final List<TrailPreview> trailPreviews;

    public TrailPreviewRestResponse(List<TrailPreview> trailPreviews, Status status, Set<String> messages) {
        super(status, messages);
        this.trailPreviews = trailPreviews;
    }

    public TrailPreviewRestResponse(List<TrailPreview> trailPreviews) {
        super(Status.OK, Collections.emptySet());
        this.trailPreviews = trailPreviews;
    }

    public List<TrailPreview> getTrailPreviews() {
        return trailPreviews;
    }
}
