package org.sc.controller;

import org.sc.common.rest.controller.*;
import org.sc.data.TrailImport;
import org.sc.data.TrailPreparationModel;
import org.sc.importer.TrailCreationValidator;
import org.sc.importer.TrailImporterManager;
import org.sc.manager.TrailManager;
import org.sc.service.GpxManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.apache.logging.log4j.util.Strings.isBlank;

@RestController
@RequestMapping(TrailController.PREFIX)
public class TrailController {

    public final static String PREFIX = "/trail";

    private final static Logger LOGGER = Logger.getLogger(TrailController.class.getName());

    public static final String FILE_INPUT_NAME = "gpxFile";
    public static final String CANNOT_READ_ERROR_MESSAGE = "Could not read GPX file.";

    public static final int BAD_REQUEST_STATUS_CODE = 400;
    public static final String EMPTY_CODE_VALUE_ERROR_MESSAGE = "Empty code value";

    public static final String TMP_FOLDER = "tmp";
    public static final File UPLOAD_DIR = new File(TMP_FOLDER);

    private final GpxManager gpxManager;
    private final TrailManager trailManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailCreationValidator trailValidator;

    @Autowired
    public TrailController(final GpxManager gpxManager,
                           final TrailManager trailManager,
                           final TrailImporterManager trailImporterManager,
                           final TrailCreationValidator trailValidator) {
        this.gpxManager = gpxManager;
        this.trailManager = trailManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
    }

    @PostMapping(path = "/read",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TrailPreparationModel readGpxFile(@RequestAttribute("file") MultipartFile gpxFile) throws IOException {
        final Path tempFile = Files.createTempFile(UPLOAD_DIR.toPath(), "", "");
        try (final InputStream input = gpxFile.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return gpxManager.getTrailPreparationFromGpx(tempFile);
    }


    @PutMapping(path = "/save",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public RESTResponse importTrail(@RequestBody TrailImport request) {
        final Set<String> errors = trailValidator.validate(request);
        final TrailRestResponse.TrailRestResponseBuilder trailRestResponseBuilder = TrailRestResponse.
                TrailRestResponseBuilder.aTrailRestResponse().withTrails(Collections.emptyList()).withMessages(errors);
        if (errors.isEmpty()) {
            trailImporterManager.save(request);
            return trailRestResponseBuilder.withStatus(Status.OK).build();
        }
        return trailRestResponseBuilder.withMessages(errors).withStatus(Status.ERROR).build();
    }

    @GetMapping
    public TrailRestResponse getAll() {
        return new TrailRestResponse(trailManager.getAll());
    }

    @GetMapping("/{code}")
    public TrailRestResponse getByCode(@PathVariable String code) {
        if(isBlank(code)) {
            return new TrailRestResponse(Collections.emptyList(), Status.ERROR, Collections.singleton("Empty code value"));
        }
        return new TrailRestResponse(trailManager.getByCode(code));
    }

    @DeleteMapping("/{code}")
    public RESTResponse deleteByCode(@PathVariable String code) {
        boolean isDeleted = trailManager.delete(code);
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No trail deleted with code '%s'", code))));
        }
    }

    @GetMapping("/download/{code}")
    public FileDownloadRestResponse getDownloadableLink(@PathVariable String code) {
        if(!StringUtils.hasText(code)) {
            return new FileDownloadRestResponse("", Status.ERROR, Collections.singleton(EMPTY_CODE_VALUE_ERROR_MESSAGE));
        }
        final List<Trail> byCode = trailManager.getByCode(code);
        if(!byCode.isEmpty()){
            return new FileDownloadRestResponse(trailManager.getDownloadableLink(code));
        }
        return new FileDownloadRestResponse("", Status.ERROR, Collections.singleton("Trail does not exist"));
    }

}
