package org.sc.common.rest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PoiResponse extends RESTResponse {
    final List<PoiDto> pois;

    public PoiResponse(List<PoiDto> pois, Status status, Set<String> messages) {
        super(status, messages);
        this.pois = pois;
    }

    public PoiResponse(List<PoiDto> pois) {
        super(Status.OK, Collections.emptySet());
        this.pois = pois;
    }

    public List<PoiDto> getPois() {
        return pois;
    }
}
