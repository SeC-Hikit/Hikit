package org.sc.frontend.controller;

import org.sc.common.rest.controller.CoordinatesWithAltitude;
import org.sc.common.rest.controller.TrailPreviewRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/preview")
public class PreviewController {

    private final TrailManager trailManager;

    @Autowired
    public PreviewController(final TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    @GetMapping
    public TrailPreviewRestResponse getTrailsPreview() throws IOException {
        return trailManager.getTrailsPreview();
    }

    @GetMapping("/{code}")
    public List<CoordinatesWithAltitude> getTrailCoordinatesByCode(@PathVariable String code) throws IOException {
        return trailManager.getTrailPreviewPoints(code);
    }
}
