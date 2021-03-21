package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.data.TrailDatasetVersion;
import org.sc.data.repository.TrailDatasetVersionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TrailDatasetController.PREFIX)
public class TrailDatasetController {

    public final static String PREFIX = "/dataset";
    private final TrailDatasetVersionDao trailDatasetVersionDao;

    @Autowired
    public TrailDatasetController(final TrailDatasetVersionDao trailDatasetVersionDao) {
        this.trailDatasetVersionDao = trailDatasetVersionDao;
    }

    @Operation(summary = "Retrieve trail dataset version")
    @GetMapping
    private TrailDatasetVersion getTrailDatasetV() {
        return trailDatasetVersionDao.getLast();
    }

}
