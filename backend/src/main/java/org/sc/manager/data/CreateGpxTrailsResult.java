package org.sc.manager.data;

import org.sc.data.model.Trail;

import java.util.LinkedHashMap;

public class CreateGpxTrailsResult {

    LinkedHashMap<String, Trail> createdTrail;


    public CreateGpxTrailsResult() {
        this.createdTrail = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Trail> getCreatedTrail() {
        return createdTrail;
    }

    public void setCreatedTrail(LinkedHashMap<String, Trail> createdTrail) {
        this.createdTrail = createdTrail;
    }

}
