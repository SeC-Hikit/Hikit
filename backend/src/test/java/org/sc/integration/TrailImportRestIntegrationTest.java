package org.sc.integration;

import org.junit.*;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.TrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.Trail;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailImportRestIntegrationTest extends ImportTrailIT {


    private static final String EXPECTED_PLACE_ID_INTERMEDIATE = "ANY_INTERMEDIATE";
    public static final String EXPECTED_TRAIL_CODE = "123BO";

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    private static final Date EXPECTED_DATE = new Date();
    public static final List<String> EXPECTED_TAGS = Arrays.asList("one", "two");
    public static final String EXPECTED_COUNTRY = "Italy";
    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;
    public static final String EXPECTED_MAINTAINANCE_SECTION = "CAI Bologna";
    public static final String EXPECTED_TERRITORIAL_DIVISION = "Porretta";
    public static final boolean IS_VARIANT = false;
    public static final int ANY_OFFICIAL_ETA = 20;

    // Start POS coordinates
    public static final TrailCoordinatesDto START_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436084, 11.315620, 250.0, 0);

    public static final TrailCoordinatesDto INTERMEDIATE_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436081, 11.315625, 250.0, 0);

    // End Pos coordinates
    public static final TrailCoordinatesDto END_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.568191623, 11.154781567, 250.0, 50);
    public static final List<TrailCoordinatesDto> EXPECTED_TRAIL_COORDINATES = Arrays.asList(
            START_EXPECTED_COORDINATE, INTERMEDIATE_EXPECTED_COORDINATE, END_EXPECTED_COORDINATE
    );
    public static final List<PlaceRefDto> SINGLETON_LIST_OF_REF_PLACES =
            singletonList(new PlaceRefDto(EXPECTED_NAME,
                    INTERMEDIATE_EXPECTED_COORDINATE, EXPECTED_PLACE_ID_INTERMEDIATE));

    // FileDetails
    public static final String ANY_FILENAME = "001xBO.gpx";
    public static final String REALM = "S&C";
    public static final String INSTANCE_ID = "BOLOGNA_1";


    public static final FileDetailsDto IMPORTED_FILE_DETAILS = new FileDetailsDto(EXPECTED_DATE, USER_ADMIN, INSTANCE_ID, REALM, ANY_FILENAME, ANY_FILENAME);

    public static List<PlaceRefDto> LOCATION_REFS;

    public TrailImportDto expectedTrailDto;

    @Autowired
    DataSource dataSource;

    @Autowired
    AdminTrailImporterController importController;
    @Autowired
    AdminPlaceController placeController;
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
        TrailResponse getTrail = trailController.getById(importedTrailId, false);
        TrailDto firstElement = getTrail.getContent().get(0);
        assertThat(getTrail.getContent().size()).isEqualTo(1);
        assertFirtElement(firstElement);
    }

    @Test
    public void getPaged_shouldFindOne() {
        TrailResponse getTrail = trailController.get(0, 0, false, REALM);
        TrailDto firstElement = getTrail.getContent().get(0);
        assertThat(getTrail.getContent().size()).isEqualTo(1);
        assertFirtElement(firstElement);
    }

    @Test
    public void delete() {
        String importedTrailId = trailResponse.getContent().get(0).getId();
        TrailResponse deletedById = adminTrailController.deleteById(importedTrailId);
        assertThat(deletedById.getContent().get(0).getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        TrailResponse getTrail = trailController.getById(importedTrailId, false);
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
        return makeCorrectTrailDtoForImport(firstPlace.getContent().get(0).getId(),
                addedPlace.getContent().get(0).getId(),
                lastPlace.getContent().get(0).getId());
    }

    public static TrailImportDto makeCorrectTrailDtoForImport(String startPlaceId, String placeId, String endPlaceId) {
        PlaceRefDto placeStartRef = new PlaceRefDto(EXPECTED_NAME,
                START_EXPECTED_COORDINATE, startPlaceId);
        PlaceRefDto endPlaceRef = new PlaceRefDto(EXPECTED_NAME,
                END_EXPECTED_COORDINATE, endPlaceId);
        LOCATION_REFS = Arrays.asList(placeStartRef, new PlaceRefDto(EXPECTED_NAME,
                INTERMEDIATE_EXPECTED_COORDINATE, placeId), endPlaceRef);

        return new TrailImportDto(EXPECTED_TRAIL_CODE, EXPECTED_NAME, EXPECTED_DESCRIPTION,
                ANY_OFFICIAL_ETA, placeStartRef, endPlaceRef, LOCATION_REFS,
                EXPECTED_TRAIL_CLASSIFICATION, EXPECTED_COUNTRY,
                EXPECTED_TRAIL_COORDINATES, REALM, IS_VARIANT, EXPECTED_TERRITORIAL_DIVISION, emptyList(), EXPECTED_DATE,
                IMPORTED_FILE_DETAILS,
                TrailStatus.RAW);
    }

}