package org.sc.manager.data;

import java.nio.file.Path;
import java.util.LinkedHashMap;

public class CreateGpxTrailsData {

    private LinkedHashMap<String, Path> filesGpxMap;

    public CreateGpxTrailsData() {
        filesGpxMap = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Path> getFilesGpxMap() {
        return filesGpxMap;
    }

    public void setFilesGpxMap(LinkedHashMap<String, Path> filesGpxMap) {
        this.filesGpxMap = filesGpxMap;
    }

}
