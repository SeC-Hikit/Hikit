package org.sc.integration;

import org.hikit.common.datasource.Datasource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.controller.TrailController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.Trail;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailImportRestIntegrationTest extends ImportTrailIT {


    public static final String EXPECTED_TRAIL_CODE = "123BO";

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    private static final Date EXPECTED_DATE = new Date();
    public static final String EXPECTED_COUNTRY = "Italy";
    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;
    public static final String EXPECTED_TERRITORIAL_DIVISION = "Porretta";
    public static final boolean IS_VARIANT = false;
    public static final int ANY_OFFICIAL_ETA = 20;

    // Start POS coordinates
    public static final TrailCoordinatesDto START_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436084, 11.315620, 250.0, 0);

    public static final TrailCoordinatesDto INTERMEDIATE_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436081, 11.315625, 250.0, 1);

    // End Pos coordinates
    public static final TrailCoordinatesDto END_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.568191623, 11.154781567, 250.0, 19478);
    public static final List<TrailCoordinatesDto> EXPECTED_TRAIL_COORDINATES = Arrays.asList(
            START_EXPECTED_COORDINATE, INTERMEDIATE_EXPECTED_COORDINATE, END_EXPECTED_COORDINATE
    );

    // FileDetails
    public static final String ANY_FILENAME = "001xBO.gpx";
    public static final String REALM = "S&C";
    public static final String INSTANCE_ID = "BOLOGNA_1";

    public static final FileDetailsDto IMPORTED_FILE_DETAILS = new FileDetailsDto(EXPECTED_DATE, USER_ADMIN, INSTANCE_ID, REALM, ANY_FILENAME, ANY_FILENAME, USER_ADMIN);

    public static List<PlaceRefDto> LOCATION_REFS;

    @Autowired
    Datasource dataSource;

    @Autowired AdminPlaceController placeController;
    @Autowired AdminTrailController adminTrailController;
    @Autowired TrailController trailController;

    private TrailResponse trailResponse;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);
        trailResponse = adminTrailController.importTrail(trailImportDto);
    }

    @Test
    public void getById_shouldFindOne() {
        String importedTrailId = trailResponse.getContent().get(0).getId();
        TrailResponse getTrail = trailController.getById(importedTrailId, TrailSimplifierLevel.FULL);
        TrailDto firstElement = getTrail.getContent().get(0);
        assertThat(getTrail.getContent().size()).isEqualTo(1);
        assertFirtElement(firstElement);
    }

    @Test
    public void getPaged_shouldFindOne() {
        TrailResponse getTrail = trailController.get(0, 1, REALM,
                TrailSimplifierLevel.FULL, true);
        TrailDto firstElement = getTrail.getContent().get(0);
        assertThat(getTrail.getContent().size()).isEqualTo(1);
        assertFirtElement(firstElement);
    }

    @Test
    public void delete() {
        String importedTrailId = trailResponse.getContent().get(0).getId();
        TrailResponse deletedById = adminTrailController.deleteById(importedTrailId);
        assertThat(deletedById.getContent().get(0).getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        TrailResponse getTrail = trailController.getById(importedTrailId, TrailSimplifierLevel.FULL);
        Assert.assertTrue(getTrail.getContent().isEmpty());
    }

    @Test
    public void contextLoads() {
        assertThat(adminTrailController).isNotNull();
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
    }

    private void assertFirtElement(TrailDto firstElement) {
        assertThat(firstElement.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(firstElement.getCoordinates()).isEqualTo(EXPECTED_TRAIL_COORDINATES);
        assertThat(firstElement.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(firstElement.getCountry()).isEqualTo(EXPECTED_COUNTRY);
        assertThat(firstElement.getLastUpdate()).isEqualToIgnoringMinutes(EXPECTED_DATE);
        assertThat(firstElement.getClassification()).isEqualTo(EXPECTED_TRAIL_CLASSIFICATION);

    }

    static TrailImportDto createThreePointsTrailImport(AdminPlaceController placeController) {
        PlaceResponse firstPlace = placeController.create(START_CORRECT_PLACE_DTO);
        PlaceResponse addedPlace = placeController.create(CORRECT_PLACE_DTO);
        PlaceResponse lastPlace = placeController.create(END_CORRECT_PLACE_DTO);
        assertThat(firstPlace.getContent()).isNotEmpty();
        assertThat(addedPlace.getContent()).isNotEmpty();
        assertThat(lastPlace.getContent()).isNotEmpty();

        PlaceDto createdFirstPlace = firstPlace.getContent().get(0);
        PlaceRefDto placeStartRef = new PlaceRefDto(createdFirstPlace.getName(),
                createdFirstPlace.getCoordinates().get(0), createdFirstPlace.getId(), emptyList(), false);

        PlaceDto createdLastPlace = lastPlace.getContent().get(0);
        PlaceRefDto placeFinalRef = new PlaceRefDto(createdLastPlace.getName(),
                createdLastPlace.getCoordinates().get(0), createdLastPlace.getId(), emptyList(), false);

        PlaceDto intermediatePlace = addedPlace.getContent().get(0);

        LOCATION_REFS = Arrays.asList(placeStartRef, new PlaceRefDto(intermediatePlace.getName(),
                intermediatePlace.getCoordinates().get(0), intermediatePlace.getId(), emptyList(), false), placeFinalRef);


        return new TrailImportDto(EXPECTED_TRAIL_CODE, EXPECTED_NAME, EXPECTED_DESCRIPTION,
                ANY_OFFICIAL_ETA, placeStartRef, placeFinalRef, LOCATION_REFS, emptyList(),
                EXPECTED_TRAIL_CLASSIFICATION, EXPECTED_COUNTRY,
                EXPECTED_TRAIL_COORDINATES, REALM, IS_VARIANT, EXPECTED_TERRITORIAL_DIVISION, emptyList(), EXPECTED_DATE,
                IMPORTED_FILE_DETAILS,
                TrailStatus.PUBLIC);
    }

}