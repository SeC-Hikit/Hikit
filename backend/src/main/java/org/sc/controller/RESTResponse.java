package org.sc.controller;

import java.util.Set;

public class RESTResponse {

    private Status status;
    private Set<String> messages;

    public RESTResponse(Status status, Set<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public Status getStatus() {
        return status;
    }

    public Set<String> getMessages() {
        return messages;
    }

}
