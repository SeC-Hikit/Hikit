package org.sc.controller;

import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.data.validator.PlaceValidator;
import org.sc.manager.PlaceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(PlaceController.PREFIX)
public class PlaceController {
    public final static String PREFIX = "/place";

    private final PlaceValidator placeValidator;
    private final PlaceManager placeManager;

    @Autowired
    public PlaceController(PlaceValidator placeValidator,
                           PlaceManager placeManager) {
        this.placeValidator = placeValidator;
        this.placeManager = placeManager;
    }

    @GetMapping
    public PlaceResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                             @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PlaceResponse(Status.OK,
                Collections.emptySet(),
                placeManager.getPaginated(page, count));
    }

    @GetMapping("/{id}")
    public PlaceResponse get(@PathVariable String id) {
        return new PlaceResponse(Status.OK,
                Collections.emptySet(),
                placeManager.getById(id));
    }

    @GetMapping("/name/{name}")
    public PlaceResponse getLikeNameOrTags(@PathVariable String name,
                                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PlaceResponse(Status.OK,
                Collections.emptySet(),
                placeManager.getLikeNameOrTags(name, page, count));
    }

    @PutMapping
    public PlaceResponse add(@RequestBody PlaceDto place) {
        Set<String> validationErrors = placeValidator.validate(place);
        if (!validationErrors.isEmpty()) {
            return new PlaceResponse(Status.ERROR, validationErrors, Collections.emptyList());
        }
        List<PlaceDto> placeDtoList = placeManager.create(place);
        return new PlaceResponse(Status.OK, Collections.emptySet(), placeDtoList);
    }

    @DeleteMapping("/{id}")
    public PlaceResponse delete(@PathVariable String id) {
        return new PlaceResponse(Status.OK, Collections.emptySet(), placeManager.deleteById(id));
    }

    @PostMapping
    public PlaceResponse update(@RequestBody PlaceDto place) {
        Set<String> validationErrors = placeValidator.validate(place);
        if (!validationErrors.isEmpty()) {
            return new PlaceResponse(Status.ERROR, validationErrors, Collections.emptyList());
        }
        List<PlaceDto> placeDtoList = placeManager.update(place);
        return new PlaceResponse(Status.OK, Collections.emptySet(), placeDtoList);
    }
}
