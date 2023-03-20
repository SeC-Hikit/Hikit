package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaticTrailDetails {
    public static final String PATH_GPX = "pathGpx";
    public static final String PATH_KML = "pathKml";
    public static final String PATH_PDF = "pathPdf";

    private String pathGpx;
    private String pathKml;
    private String pathPdf;
}
