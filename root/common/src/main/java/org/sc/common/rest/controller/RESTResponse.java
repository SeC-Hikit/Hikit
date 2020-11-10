package org.sc.common.rest.controller;

import java.util.Collections;
import java.util.Set;

public class RESTResponse {

    private Status status;
    private Set<String> messages;

    public RESTResponse(Status status, Set<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public RESTResponse(Set<String> messages) {
        this.status = Status.ERROR;
        this.messages = messages;
    }

    public RESTResponse() {
        this.status = Status.OK;
        this.messages = Collections.emptySet();
    }

    public Status getStatus() {
        return status;
    }

    public Set<String> getMessages() {
        return messages;
    }

}
