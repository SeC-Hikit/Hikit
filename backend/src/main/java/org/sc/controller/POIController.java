package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.PoiResponse;
import org.sc.data.validator.LinkedMediaValidator;
import org.sc.data.validator.MediaExistenceValidator;
import org.sc.data.validator.poi.PoiExistenceValidator;
import org.sc.data.validator.poi.PoiValidator;
import org.sc.manager.PoiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(POIController.PREFIX)
public class POIController {

    public final static String PREFIX = "/poi";
    private final static Logger LOGGER = Logger.getLogger(POIController.class.getName());

    private final PoiManager poiManager;
    private final PoiValidator poiValidator;
    private final PoiExistenceValidator poiExistenceValidator;
    private final LinkedMediaValidator linkedMediaValidator;
    private final MediaExistenceValidator mediaExistanceValidator;

    @Autowired
    public POIController(final PoiManager poiManager,
                         final PoiValidator poiValidator,
                         final PoiExistenceValidator poiExistenceValidator,
                         final LinkedMediaValidator linkedMediaValidator,
                         final MediaExistenceValidator mediaExistanceValidator) {
        this.poiManager = poiManager;
        this.poiValidator = poiValidator;
        this.poiExistenceValidator = poiExistenceValidator;
        this.linkedMediaValidator = linkedMediaValidator;
        this.mediaExistanceValidator = mediaExistanceValidator;
    }


    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = poiManager.countPoi();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @GetMapping
    public PoiResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PoiResponse(Status.OK,
                Collections.emptySet(),
                poiManager.getPoiPaginated(page, count));
    }

    @GetMapping("/{id}")
    public PoiResponse get(@PathVariable String id) {
        return new PoiResponse(Status.OK,
                Collections.emptySet(),
                poiManager.getPoiByID(id));
    }

    @GetMapping("/code/{code}")
    public PoiResponse getByTrail(@PathVariable String code,
                                  @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PoiResponse(Status.OK,
                Collections.emptySet(),
                poiManager.getPoiByTrailId(code, page, count));
    }

    @GetMapping("/type/{type}")
    public PoiResponse getByMacro(@PathVariable String type,
                                  @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PoiResponse(Status.OK,
                Collections.emptySet(),
                poiManager.getPoiByMacro(type, page, count));
    }

    @GetMapping("/name/{name}")
    public PoiResponse getByNameOrTags(@PathVariable String name,
                                       @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                       @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PoiResponse(Status.OK,
                Collections.emptySet(),
                poiManager.getPoiByName(name, page, count));
    }

    @PutMapping
    public PoiResponse upsertPoi(@RequestBody PoiDto poiDto) {
        final Set<String> errors = poiValidator.validate(poiDto);
        if (errors.isEmpty()) {
            final List<PoiDto> poiDtos = poiManager.upsertPoi(poiDto);
            return new PoiResponse(Status.OK, errors, poiDtos);
        }
        return new PoiResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @PutMapping("/media/{id}")
    public PoiResponse addMediaToPoi(@PathVariable String id,
                                     @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = poiExistenceValidator.validate(id);
        errors.addAll(linkedMediaValidator.validate(linkedMediaRequest));
        if (errors.isEmpty()) {
            final List<PoiDto> poiDtos =
                    poiManager.linkMedia(id, linkedMediaRequest);
            return new PoiResponse(Status.OK, Collections.emptySet(), poiDtos);
        }
        return new PoiResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @DeleteMapping("/media/{id}")
    public PoiResponse removeMediaFromPoi(@PathVariable String id,
                                          @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = poiExistenceValidator.validate(id);
        errors.addAll(mediaExistanceValidator.validate(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            return new PoiResponse(Status.OK, errors, poiManager.unlinkMedia(id, unLinkeMediaRequestDto));
        }
        return new PoiResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @DeleteMapping("/{id}")
    public PoiResponse deletePoi(@PathVariable String id) {
        List<PoiDto> deleted = poiManager.deleteById(id);
        if (deleted.isEmpty()) {
            LOGGER.warning(format("Could not delete POI with id '%s'", id));
            return new PoiResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No POI was found with id '%s'", id))), Collections.emptyList());
        }
        return new PoiResponse(Status.OK, Collections.emptySet(), deleted);
    }


}
