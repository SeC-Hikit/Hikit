package org.sc.common.rest;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaticTrailDetailsDto {
    private String pathGpx;
    private String pathKml;
    private String pathPdf;
}

