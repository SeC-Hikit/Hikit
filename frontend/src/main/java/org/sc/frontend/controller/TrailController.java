package org.sc.frontend.controller;

import org.sc.common.rest.controller.TrailPreparationModel;
import org.sc.common.rest.controller.TrailRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/trail")
public class TrailController {

    private final TrailManager trailManager;

    @Autowired
    public TrailController(final TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    @GetMapping
    public TrailRestResponse getTrailsCoordinateLow() throws IOException {
        return trailManager.getTrailsLowCoordinates();
    }

    @GetMapping("/{code}")
    private TrailRestResponse getTrailByCode(@PathVariable String code) throws IOException {
        return trailManager.getTrail(code);
    }

    @GetMapping(value = "/download/{code}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    private @ResponseBody byte[] getDownloadableFile(@PathVariable String code) throws IOException {
        return trailManager.getTrailDownloadableLink(code);
    }

    @PutMapping(value = "/import/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TrailPreparationModel readGpxFile(@RequestAttribute MultipartFile gpxFile) throws IOException {
        return trailManager.getTrailPreparationFromGpx(gpxFile);
    }

}
