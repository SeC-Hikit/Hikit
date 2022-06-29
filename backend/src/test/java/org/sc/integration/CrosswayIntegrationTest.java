package org.sc.integration;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.GeoLineDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.GeoTrailController;
import org.sc.controller.PlaceController;
import org.sc.controller.TrailController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.Coordinates2D;
import org.sc.data.model.TrailStatus;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sc.common.rest.Status.*;
import static org.sc.integration.TrailImportRestIntegrationTest.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CrosswayIntegrationTest extends ImportTrailIT {

    public static final String LEVEL = TrailSimplifierLevel.FULL.toString();
    public static final int TWELVE_SECONDS = 12000;
    public static final String EXPECTED_NAME = "Crocevia 100BO, 101BO";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AdminPlaceController adminPlaceController;
    @Autowired
    private PlaceController placeController;
    @Autowired
    private AdminTrailController adminTrailController;
    @Autowired
    private TrailController trailController;
    @Autowired
    private GeoTrailController geoTrailController;

    private TrailResponse importedTrailResponse;
    private TrailResponse crossingImportedTrailResponse;

    private TrailDto importedTrail;
    private TrailDto crossingImportedTrail;
    private PlaceDto createdCrosswayPlace;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        createFirstTrailWithAutoCrossway();

        PlaceResponse firstPlace = adminPlaceController.create(START_CORRECT_PLACE_DTO);
        PlaceResponse lastPlace = adminPlaceController.create(END_CORRECT_PLACE_DTO);
        Assertions.assertThat(firstPlace.getContent()).isNotEmpty();
        Assertions.assertThat(lastPlace.getContent()).isNotEmpty();

        PlaceDto createdFirstPlace = firstPlace.getContent().get(0);
        PlaceRefDto placeStartRef = new PlaceRefDto(createdFirstPlace.getName(),
                createdFirstPlace.getCoordinates().get(0), createdFirstPlace.getId(), emptyList(), false);

        PlaceDto createdLastPlace = lastPlace.getContent().get(0);
        PlaceRefDto placeFinalRef = new PlaceRefDto(createdLastPlace.getName(),
                createdLastPlace.getCoordinates().get(0), createdLastPlace.getId(), emptyList(), false);

        TrailIntersectionResponse trailIntersection = geoTrailController.findTrailIntersection(new GeoLineDto(
                asList(
                        new Coordinates2D(START_COORDINATES_DTO_2.getLongitude(), START_COORDINATES_DTO_2.getLatitude()),
                        new Coordinates2D(END_COORDINATES_DTO_2.getLongitude(), END_COORDINATES_DTO_2.getLatitude())
                )), 0, 1);


        TrailIntersectionDto trailIntersectionDto
                = trailIntersection.getContent().stream().findFirst().get();
        TrailDto trail = trailIntersectionDto.getTrail();

        PlaceResponse placeResponse = placeController.geolocatePlace(PointGeolocationDto.builder()
                .coordinatesDto(new CoordinatesDto(44.491557, 11.248103))
                .distance(200L).build(), 0, 1);

        PlaceDto placeDto = placeResponse.getContent().stream().findFirst().get();

        CoordinatesDto coordinates = placeDto.getCoordinates().stream().findFirst().get();
        PlaceRefDto crosswayRefDto =
                PlaceRefDto.builder()
                        .encounteredTrailIds(Collections.singletonList(trail.getId()))
                        .placeId(placeDto.getId()).dynamicCrossway(placeDto.isDynamic())
                        .coordinates(coordinates).build();

        TrailImportDto crossingTrail = new TrailImportDto(
                "101BO",
                "crosswayTrail",
                "crossing trail desc",
                ANY_OFFICIAL_ETA, placeStartRef,
                placeFinalRef, asList(placeStartRef, crosswayRefDto, placeFinalRef), emptyList(),
                EXPECTED_TRAIL_CLASSIFICATION,
                EXPECTED_COUNTRY,
                EXPECTED_TRAIL_COORDINATES, REALM,
                IS_VARIANT, EXPECTED_TERRITORIAL_DIVISION,
                emptyList(), new Date(),
                IMPORTED_FILE_DETAILS,
                TrailStatus.PUBLIC);

        crossingImportedTrailResponse = adminTrailController.importTrail(crossingTrail);
        crossingImportedTrail = crossingImportedTrailResponse.getContent().stream().findFirst().get();
    }

    @Test
    public void shouldUpdateAutomaticCrossway() {
        final TrailResponse retrievedInitialTrailResp = trailController.getById(importedTrail.getId(), TrailSimplifierLevel.LOW);
        final TrailResponse retrievedCrossingTrailResp = trailController.getById(crossingImportedTrail.getId(), TrailSimplifierLevel.LOW);
        final TrailDto retrievedTrail = retrievedInitialTrailResp.getContent().stream().findFirst().get();
        final TrailDto retrievedCrossingTrail = retrievedCrossingTrailResp.getContent().stream().findFirst().get();

        final String placeId = createdCrosswayPlace.getId();
        final PlaceResponse placeByIdResponse = placeController.get(placeId);
        final PlaceDto placeDto = placeByIdResponse.getContent().get(0);

        assertThat(placeDto.getName()).isEqualTo(EXPECTED_NAME);
        assertThat((retrievedTrail)
                .getLocations().stream().filter(it-> it.getPlaceId().equals(placeId))
                .collect(Collectors.toList()).stream().findFirst().get().getName())
                .isEqualTo(EXPECTED_NAME);
        assertThat((retrievedCrossingTrail)
                .getLocations().stream()
                .filter(it-> it.getPlaceId().equals(placeId)).collect(Collectors.toList()).stream().findFirst().get().getName())
                .isEqualTo(EXPECTED_NAME);
    }

    @Test
    public void shouldEnsureCrosswayDeletionWhenNoTrailsCrossIt() {
        final TrailResponse retrievedInitialTrailResp = trailController.getById(importedTrail.getId(), TrailSimplifierLevel.LOW);
        final TrailResponse retrievedCrossingTrailResp = trailController.getById(crossingImportedTrail.getId(), TrailSimplifierLevel.LOW);

        assertEquals(OK, retrievedInitialTrailResp.getStatus());
        assertEquals(OK, retrievedCrossingTrailResp.getStatus());

        final String placeId = createdCrosswayPlace.getId();
        final PlaceResponse placeByIdResponse = placeController.get(placeId);
        final PlaceDto placeDto = placeByIdResponse.getContent().get(0);

        assertThat(placeDto.getName()).isEqualTo(EXPECTED_NAME);

        adminTrailController.deleteById(importedTrail.getId());
        adminTrailController.deleteById(crossingImportedTrail.getId());

        final PlaceResponse placeResponseAfterTrailsDeletion = placeController.get(placeId);
        assertThat(placeResponseAfterTrailsDeletion.getContent().isEmpty()).isEqualTo(true);
    }

    private void createFirstTrailWithAutoCrossway() {
        PlaceResponse firstPlace = adminPlaceController.create(START_CORRECT_PLACE_DTO_2);
        PlaceResponse crosswayPlace = adminPlaceController.create(MID_AUTO_CROSSWAY);
        PlaceResponse lastPlace = adminPlaceController.create(END_CORRECT_PLACE_DTO_2);

        Assertions.assertThat(firstPlace.getContent()).isNotEmpty();
        Assertions.assertThat(crosswayPlace.getContent()).isNotEmpty();
        Assertions.assertThat(lastPlace.getContent()).isNotEmpty();

        PlaceDto createdFirstPlace = firstPlace.getContent().stream().findFirst().get();
        PlaceRefDto placeStartRef = new PlaceRefDto(createdFirstPlace.getName(),
                createdFirstPlace.getCoordinates().get(0), createdFirstPlace.getId(), emptyList(), false);

        createdCrosswayPlace = crosswayPlace.getContent().get(0);
        PlaceRefDto crosswayRef = new PlaceRefDto(createdCrosswayPlace.getName(),
                createdCrosswayPlace.getCoordinates().get(0), createdCrosswayPlace.getId(), emptyList(), true);

        PlaceDto createdLastPlace = lastPlace.getContent().get(0);
        PlaceRefDto placeFinalRef = new PlaceRefDto(createdLastPlace.getName(),
                createdLastPlace.getCoordinates().get(0), createdLastPlace.getId(), emptyList(), false);

        final TrailImportDto crossingTrail = new TrailImportDto(
                "100BO",
                "crosswayTrail",
                "crossing trail desc",
                ANY_OFFICIAL_ETA, placeStartRef,
                placeFinalRef, asList(placeStartRef, crosswayRef, placeFinalRef), emptyList(),
                EXPECTED_TRAIL_CLASSIFICATION,
                EXPECTED_COUNTRY,
                EXPECTED_TRAIL_COORDINATES, REALM,
                IS_VARIANT, EXPECTED_TERRITORIAL_DIVISION,
                emptyList(), new Date(),
                IMPORTED_FILE_DETAILS,
                TrailStatus.PUBLIC);

        importedTrailResponse = adminTrailController.importTrail(crossingTrail);
        importedTrail = importedTrailResponse.getContent().stream().findFirst().get();
    }


}
