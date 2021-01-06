package org.sc.common.rest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TrailResponse extends RESTResponse {
    final List<Trail> trails;

    public TrailResponse(List<Trail> trails, Status status, Set<String> messages) {
        super(status, messages);
        this.trails = trails;
    }

    public TrailResponse(List<Trail> trails) {
        super(Status.OK, Collections.emptySet());
        this.trails = trails;
    }

    public List<Trail> getTrails() {
        return trails;
    }

    public static final class TrailRestResponseBuilder {
        private List<Trail> trails;
        private Status status;
        private Set<String> messages;

        private TrailRestResponseBuilder() {
        }

        public static TrailRestResponseBuilder aTrailRestResponse() {
            return new TrailRestResponseBuilder();
        }

        public TrailRestResponseBuilder withTrails(List<Trail> trails) {
            this.trails = trails;
            return this;
        }

        public TrailRestResponseBuilder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public TrailRestResponseBuilder withMessages(Set<String> messages) {
            this.messages = messages;
            return this;
        }

        public TrailResponse build() {
            return new TrailResponse(trails, status, messages);
        }
    }
}
