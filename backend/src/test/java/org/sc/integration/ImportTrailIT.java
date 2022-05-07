package org.sc.integration;

import org.sc.common.rest.*;
import org.sc.data.model.TrailClassification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ImportTrailIT {

    public static final String USER_ADMIN = "mario";

    public static final CoordinatesDto START_COORDINATES_DTO = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final CoordinatesDto INTERMEDIATE_COORDINATES_DTO = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final CoordinatesDto END_COORDINATES_DTO = new CoordinatesDto(44.568191623, 11.154781567, 250.0);

    public static final CoordinatesDto INTERMEDIATE_EXPECTED_COORDINATE = new CoordinatesDto(44.436084, 11.315620, 250.0);

    public static final String PLACE_EXPECTED_DESCRIPTION = "<p>ANY_DESCRIPTION</p>";
    public static final List<String> TAGS = Arrays.asList("Magic", "Place");


    public static PlaceDto START_CORRECT_PLACE_DTO = new PlaceDto(null, "The first magical place", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(START_COORDINATES_DTO), Collections.emptyList(), false, null);

    public static final String PLACE_NAME = "A magical place";

    public static PlaceDto CORRECT_PLACE_DTO = new PlaceDto(null, PLACE_NAME, PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(INTERMEDIATE_COORDINATES_DTO), Collections.emptyList(), false, null);

    public static PlaceDto END_CORRECT_PLACE_DTO = new PlaceDto(null, "Another magical place", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(END_COORDINATES_DTO), Collections.emptyList(), false,null);


}
