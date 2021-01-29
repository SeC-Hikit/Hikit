package org.sc.controller;

import org.sc.common.rest.Status;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.response.TrailResponse;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailController.PREFIX)
public class TrailController {

    public final static String PREFIX = "/trail";

    private final TrailManager trailManager;

    @Autowired
    public TrailController(final TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count,
            @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(Status.OK, Collections.emptySet(), trailManager.get(light, page, count));
    }

    @GetMapping("/{code}")
    public TrailResponse getByCode(@PathVariable String code,
                                   @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(Status.OK, Collections.emptySet(), trailManager.getByCode(code, light));
    }

    @DeleteMapping("/{code}")
    public TrailResponse deleteByCode(@PathVariable String code,
                                     @RequestParam(required = false, defaultValue = "false") boolean isPurged) {
        List<TrailDto> deleted = trailManager.delete(code, isPurged);
        if (!deleted.isEmpty()) {
            return new TrailResponse(Status.OK, Collections.emptySet(), deleted);
        } else {
            return new TrailResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No trail deleted with code '%s'", code))), Collections.emptyList());
        }
    }
}
