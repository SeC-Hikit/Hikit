package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.adapter.AltitudeServiceAdapter;
import org.sc.common.rest.CoordinatesDto;
import org.sc.data.validator.GeneralValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(GeoToolsController.PREFIX)
public class GeoToolsController {

    public final static String PREFIX = "/geo-tool";

    private final GeneralValidator generalValidator;
    private final AltitudeServiceAdapter altitudeServiceAdapter;


    @Autowired
    public GeoToolsController(final GeneralValidator generalValidator,
                              final AltitudeServiceAdapter altitudeServiceAdapter) {
        this.generalValidator = generalValidator;
        this.altitudeServiceAdapter = altitudeServiceAdapter;
    }

    @Operation(summary = "Find a point elevation by lat-long")
    @GetMapping("/altitude")
    public CoordinatesDto geoLocateTrail(@RequestParam double latitude,
                                         @RequestParam double longitude) {
        final Set<String> errors = generalValidator.validate(new CoordinatesDto(latitude, longitude));
        if(!errors.isEmpty()) {
            return new CoordinatesDto();
        }
        final double altitudeByLongLat = altitudeServiceAdapter.getAltitudeByLongLat(latitude, longitude);
        return new CoordinatesDto(latitude, longitude, altitudeByLongLat);
    }
}
