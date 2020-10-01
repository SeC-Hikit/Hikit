package org.sc.controller;

import com.google.inject.Inject;
import org.sc.GpxManager;
import org.sc.data.Trail;
import org.sc.data.TrailDAO;
import org.sc.data.TrailPreparationModel;
import org.sc.data.helper.GsonBeanHelper;
import org.sc.data.helper.JsonHelper;
import org.sc.importer.TrailCreationValidator;
import org.sc.importer.TrailImporterManager;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.sc.configuration.ConfigurationManager.TMP_FOLDER;
import static org.sc.configuration.ConfigurationManager.UPLOAD_DIR;
import static org.sc.configuration.ConfigurationProperties.ACCEPT_TYPE;
import static org.sc.configuration.ConfigurationProperties.API_PREFIX;
import static spark.Spark.post;
import static spark.Spark.put;

public class ImporterController implements PublicController {

    public static final String MULTI_PART_JETTY_CONFIG = "org.eclipse.jetty.multipartConfig";

    private final static Logger LOGGER = Logger.getLogger(ImporterController.class.getName());
    private final static String PREFIX = API_PREFIX + "/trails";

    public static final String FILE_INPUT_NAME = "gpxFile";
    public static final String CANNOT_READ_ERROR_MESSAGE = "Could not read GPX file.";

    public static final int BAD_REQUEST_STATUS_CODE = 400;

    private final GpxManager gpxManager;
    private final GsonBeanHelper gsonBeanHelper;
    private final TrailImporterManager trailImporterManager;
    private final TrailCreationValidator trailValidator;
    private final TrailDAO trailDAO;

    @Inject
    public ImporterController(final GpxManager gpxManager,
                              final GsonBeanHelper gsonBeanHelper,
                              final TrailImporterManager trailImporterManager,
                              final TrailCreationValidator trailValidator,
                              final TrailDAO trailDAO) {
        this.gpxManager = gpxManager;
        this.gsonBeanHelper = gsonBeanHelper;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
        this.trailDAO = trailDAO;
    }

    // trails/gpx
    private TrailPreparationModel readGpxFile(final Request request,
                                              final Response response) throws IOException {
        response.type(ACCEPT_TYPE);
        final Path tempFile = Files.createTempFile(UPLOAD_DIR.toPath(), "", "");
        request.attribute(MULTI_PART_JETTY_CONFIG, new MultipartConfigElement(String.format("/%s", TMP_FOLDER)));

        try (final InputStream input = request.raw().getPart(FILE_INPUT_NAME).getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (final ServletException e) {
            LOGGER.warning(CANNOT_READ_ERROR_MESSAGE + e.getMessage());
        }
        return gpxManager.getTrailPreparationFromGpx(tempFile);
    }

    // trails/import
    private RESTResponse importTrail(final Request request,
                                     final Response response) {
        response.type(ACCEPT_TYPE);
        final Set<String> errors = trailValidator.validate(request);
        final Trail trailRequest = convertRequestToTrail(request);
        final TrailRestResponse.TrailRestResponseBuilder trailRestResponseBuilder = TrailRestResponse.
                TrailRestResponseBuilder.aTrailRestResponse().withMessages(errors);
        if (errors.isEmpty()) {
            trailDAO.upsertTrail(trailRequest);
            return trailRestResponseBuilder.withStatus(Status.OK).build();
        }
        response.status(BAD_REQUEST_STATUS_CODE);
        return trailRestResponseBuilder.withMessages(errors).withStatus(Status.ERROR).build();
    }

    private Trail convertRequestToTrail(final Request request) {
        final String requestBody = request.body();
        return Objects.requireNonNull(gsonBeanHelper.getGsonBuilder())
                .fromJson(requestBody, Trail.class);
    }

    public void init() {
        post(format("%s/gpx", PREFIX), this::readGpxFile, JsonHelper.json());
        put(format("%s/import", PREFIX), this::importTrail, JsonHelper.json());
    }

}
