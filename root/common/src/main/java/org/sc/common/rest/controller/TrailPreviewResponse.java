package org.sc.common.rest.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TrailPreviewResponse extends RESTResponse {
    final List<TrailPreview> trailPreviews;

    public TrailPreviewResponse(final List<TrailPreview> trailPreviews,
                                final Status status,
                                final Set<String> messages) {
        super(status, messages);
        this.trailPreviews = trailPreviews;
    }

    public TrailPreviewResponse(List<TrailPreview> trailPreviews) {
        super(Status.OK, Collections.emptySet());
        this.trailPreviews = trailPreviews;
    }

    public List<TrailPreview> getTrailPreviews() {
        return trailPreviews;
    }
}
