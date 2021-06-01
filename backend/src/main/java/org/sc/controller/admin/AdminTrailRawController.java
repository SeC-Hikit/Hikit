package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.response.TrailRawResponseHelper;
import org.sc.manager.TrailRawManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static org.sc.controller.admin.Constants.PREFIX_RAW;

@RestController
@RequestMapping(PREFIX_RAW)
public class AdminTrailRawController {

    private final TrailRawManager trailRawManager;
    private final TrailRawResponseHelper trailRawResponseHelper;

    @Autowired
    public AdminTrailRawController(final TrailRawManager trailRawManager,
                              final TrailRawResponseHelper trailRawResponseHelper) {
        this.trailRawManager = trailRawManager;
        this.trailRawResponseHelper = trailRawResponseHelper;
    }

    @Operation(summary = "Delete a single raw trail")
    @DeleteMapping("/{id}")
    public TrailRawResponse deleteById(final @PathVariable String id) {
        return trailRawResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailRawManager.deleteById(id),
                        trailRawManager.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }
}
