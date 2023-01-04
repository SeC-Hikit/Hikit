package org.sc.integration;

import org.sc.common.rest.CoordinatesDto;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.admin.AdminTrailImporterController;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImportTrailIT {

    public static final String USER_ADMIN = "mario";

    public static final CoordinatesDto START_COORDINATES_DTO = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final CoordinatesDto INTERMEDIATE_COORDINATES_DTO = new CoordinatesDto(44.491557, 11.248103, 250.0);
    public static final CoordinatesDto END_COORDINATES_DTO = new CoordinatesDto(44.568191623, 11.154781567, 250.0);

    public static final CoordinatesDto START_COORDINATES_DTO_2 = new CoordinatesDto(44.486450, 11.232650, 100.0);
    public static final CoordinatesDto END_COORDINATES_DTO_2 = new CoordinatesDto(44.495138, 11.258401, 180.0);

    public static final CoordinatesDto INTERMEDIATE_EXPECTED_COORDINATE = new CoordinatesDto(44.436084, 11.315620, 250.0);

    public static final String PLACE_EXPECTED_DESCRIPTION = "<p>ANY_DESCRIPTION</p>";
    public static final List<String> TAGS = Arrays.asList("Magic", "Place");


    public static PlaceDto START_CORRECT_PLACE_DTO = new PlaceDto(null, "The first magical place", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(START_COORDINATES_DTO), Collections.emptyList(), false, null);


    public static PlaceDto START_CORRECT_PLACE_DTO_2 = new PlaceDto(null, "Another trail start", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(START_COORDINATES_DTO_2), Collections.emptyList(), false, null);

    public static PlaceDto MID_AUTO_CROSSWAY = new PlaceDto(null, "Automatic crossway", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(INTERMEDIATE_COORDINATES_DTO), Collections.emptyList(), true, null);

    public static PlaceDto END_CORRECT_PLACE_DTO_2 = new PlaceDto(null, "Another trail end", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(END_COORDINATES_DTO_2), Collections.emptyList(), false,null);

    public static final String PLACE_NAME = "A magical place";

    public static PlaceDto CORRECT_PLACE_DTO = new PlaceDto(null, PLACE_NAME, PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(INTERMEDIATE_COORDINATES_DTO), Collections.emptyList(), false, null);

    public static PlaceDto END_CORRECT_PLACE_DTO = new PlaceDto(null, "Another magical place", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(END_COORDINATES_DTO), Collections.emptyList(), false,null);

    public static TrailRawResponse importRawTrail(final AdminTrailImporterController adminTrailImporterController,
                                           final String fileName, final Class<?> clazz) throws IOException {
        return adminTrailImporterController.importGpx(
                new MockMultipartFile("file", fileName, "multipart/form-data",
                        clazz.getClassLoader().getResourceAsStream("trails" + File.separator + fileName)
                )
        );
    }
}
