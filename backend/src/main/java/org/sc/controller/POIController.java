package org.sc.controller;

import org.sc.common.rest.PoiDto;
import org.sc.common.rest.PoiResponse;
import org.sc.common.rest.RESTResponse;
import org.sc.common.rest.Status;
import org.sc.data.validator.PoiValidator;
import org.sc.manager.PoiManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
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

    public POIController(final PoiManager poiManager,
                         final PoiValidator poiValidator) {
        this.poiManager = poiManager;
        this.poiValidator = poiValidator;
    }

    @GetMapping("/")
    public PoiResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                    @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count){
        return new PoiResponse(poiManager.getPoiPaginated(page, count));
    }

    @GetMapping("/{id}")
    public PoiResponse get(@PathVariable String id,
                    @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                    @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count){
        return new PoiResponse(poiManager.getPoiByID(id));
    }

    @GetMapping("/code/{code}")
    public PoiResponse getByTrail(@PathVariable String code,
                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count){
        return new PoiResponse(poiManager.getPoiByTrailCode(code, page, count));
    }

    @GetMapping("/type/{type}")
    public PoiResponse getByMacro(@PathVariable String type,
                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PoiResponse(poiManager.getPoiByMacro(type, page, count));
    }

    @GetMapping("/name/{name}")
    public PoiResponse getLikeNameAndTags(@PathVariable String name,
                                   @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                   @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PoiResponse(poiManager.getPoiByName(name, page, count));
    }

    @PutMapping
    public RESTResponse upsertPoi(@RequestBody PoiDto poiDto) {
        final Set<String> errors = poiValidator.validate(poiDto);
        if(errors.isEmpty()){
            PoiManager poiManager = this.poiManager;
            poiManager.upsertPoi(poiDto);
        }
        return new RESTResponse(errors);
    }

    @DeleteMapping("/{id}")
    public RESTResponse deletePoi(@PathVariable String id) {
        boolean isDeleted = poiManager.deleteById(id);
        if(!isDeleted){
            LOGGER.warning(format("Could not delete maintenance with id '%s'", id));
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No POI was found with id '%s'", id))));
        }
        return new RESTResponse();

    }


}
