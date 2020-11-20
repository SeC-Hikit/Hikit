package org.sc.frontend.controller;

import org.sc.common.rest.controller.TrailRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

}
