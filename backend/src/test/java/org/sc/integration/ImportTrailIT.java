package org.sc.integration;

import org.sc.common.rest.CoordinatesDto;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.PlaceRefDto;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.configuration.DataSource;
import org.sc.data.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ImportTrailIT {

    private static final String EXPECTED_PLACE_ID = "ANY_P1";
    private static final String EXPECTED_PLACE_ID2 = "ANY_P2";
    private static final String EXPECTED_ID = "ANY";
    private static final String EXPECTED_ID2 = "ANY2";
    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_NAME_2 = "ANY_2";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    public static final String EXPECTED_TRAIL_CODE = "125BO";
    private static final Date EXPECTED_DATE = new Date();
    public static final List<String> EXPECTED_TAGS = Arrays.asList("one", "two");
    public static final List<String> EXPECTED_TAGS_2 = Arrays.asList("three", "four");
    public static final String EXPECTED_COUNTRY = "Italy";
    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;
    public static final String EXPECTED_MAINTAINANCE_SECTION = "CAI Bologna";


    public static final CoordinatesDto START_COORDINATES_DTO = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final CoordinatesDto INTERMEDIATE_COORDINATES_DTO = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final CoordinatesDto INTERMEDIATE_COORDINATES_DTO_2 = new CoordinatesDto(44.436087, 11.315623, 230.0);
    public static final CoordinatesDto END_COORDINATES_DTO = new CoordinatesDto(44.568191623, 11.154781567, 250.0);

    public static final TrailCoordinatesDto START_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436084, 11.315620, 250.0, 0);
    public static final TrailCoordinatesDto INTERMEDIATE_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436084, 11.315620, 250.0, 20);
    public static final TrailCoordinatesDto END_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.568191623, 11.154781567, 250.0, 50);

    public static final PlaceRefDto START_REF_COORDINATE = new PlaceRefDto(EXPECTED_NAME, START_EXPECTED_COORDINATE, EXPECTED_PLACE_ID);
    public static final PlaceRefDto END_REF_COORDINATE = new PlaceRefDto(EXPECTED_NAME_2, END_EXPECTED_COORDINATE, EXPECTED_PLACE_ID2);

    public static final PlaceDto EXPECTED_START_POS = new PlaceDto(EXPECTED_ID, EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_TAGS, emptyList(), singletonList(START_COORDINATES_DTO), emptyList());
    public static final PlaceDto EXPECTED_FINAL_POS = new PlaceDto(EXPECTED_ID2, EXPECTED_NAME_2, EXPECTED_DESCRIPTION, EXPECTED_TAGS_2, emptyList(), singletonList(END_COORDINATES_DTO), emptyList());


    public static final String PLACE_EXPECTED_DESCRIPTION = "<p>ANY_DESCRIPTION</p>";
    public static final List<String> TAGS = Arrays.asList("Magic", "Place");


    public static PlaceDto START_CORRECT_PLACE_DTO = new PlaceDto(null, "The first magical place", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(START_COORDINATES_DTO), Collections.emptyList());

    public static final String PLACE_NAME = "A magical place";
    public static PlaceDto CORRECT_PLACE_DTO = new PlaceDto(null, PLACE_NAME, PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(INTERMEDIATE_COORDINATES_DTO), Collections.emptyList());

    public static PlaceDto ANOTHER_CORRECT_PLACE_DTO = new PlaceDto(null, PLACE_NAME, PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(INTERMEDIATE_COORDINATES_DTO_2), Collections.emptyList());


    public static PlaceDto END_CORRECT_PLACE_DTO = new PlaceDto(null, "Another magical place", PLACE_EXPECTED_DESCRIPTION,
            TAGS, Collections.emptyList(),
            Collections.singletonList(END_COORDINATES_DTO), Collections.emptyList());


}
