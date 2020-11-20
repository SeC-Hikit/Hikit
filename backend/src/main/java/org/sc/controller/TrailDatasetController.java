package org.sc.controller;

import org.sc.data.TrailDatasetVersion;
import org.sc.data.TrailDatasetVersionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(TrailDatasetController.PREFIX)
public class TrailDatasetController {

    public final static String PREFIX = "/dataset";
    private TrailDatasetVersionDao trailDatasetVersionDao;

    @Autowired
    public TrailDatasetController(TrailDatasetVersionDao trailDatasetVersionDao) {
        this.trailDatasetVersionDao = trailDatasetVersionDao;
    }

    @GetMapping
    private TrailDatasetVersion getTrailDatasetV() {
        return trailDatasetVersionDao.getLast();
    }

}
