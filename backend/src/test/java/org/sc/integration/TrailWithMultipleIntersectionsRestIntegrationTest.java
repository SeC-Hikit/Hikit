package org.sc.integration;

import org.hikit.common.datasource.Datasource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.GeoLineDto;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.controller.GeoTrailController;
import org.sc.controller.PlaceController;
import org.sc.controller.TrailController;
import org.sc.controller.TrailRawController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.controller.admin.AdminTrailRawController;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailWithMultipleIntersectionsRestIntegrationTest {

    public static final String TRAIL_INTERSECTION_FOLDER = "multiple_intersections";
    public static final String TRAIL_345_IMPORT_FILENAME = "345.gpx";
    public static final String TRAIL_400_4BO_IMPORT_FILENAME = "400-4.gpx";
    public static final String TRAIL_446_IMPORT_FILENAME = "446.gpx";

    @Autowired
    Datasource dataSource;
    @Autowired
    TrailRawController trailRawController;
    @Autowired
    AdminTrailImporterController adminTrailImporterController;
    @Autowired
    AdminTrailRawController adminTrailRawController;
    @Autowired
    AdminPlaceController adminPlaceController;
    @Autowired
    AdminTrailController adminTrailController;
    @Autowired
    TrailController trailController;
    @Autowired
    GeoTrailController geoTrailController;
    @Autowired
    PlaceController placeController;

    private TrailRawResponse trail345Import;
    private TrailRawResponse trail400_4Import;

    private TrailDto trail001BO;
    private TrailDto trail001aBO;
    private PlaceRefDto intersectionPlaceRef;
    private TrailRawResponse trail446Import;


    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail345Import = IntegrationUtils.importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_345_IMPORT_FILENAME, this.getClass());
        trail400_4Import = IntegrationUtils.importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_400_4BO_IMPORT_FILENAME, this.getClass());
        trail446Import = IntegrationUtils.importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_446_IMPORT_FILENAME, this.getClass());
    }

    @Test
    public void onCrosswayDetection_shouldDetectTwoIntersectionsWithSameTrail() {

        TrailRawResponse trail345RawResp = trailRawController.getById(trail345Import.getContent().stream().findFirst().get().getId());
        assertThat(trail345RawResp.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = trail345RawResp.getContent().get(0);

        String startPlaceAndCrossway = "Rocca Corneta";
        String endPlace = "Abetaia";

        // 345aBO: goes from CROCEVIA -> MONTE BADUCCO
        PlaceRefDto startPlaceCrocevia = new PlaceRefDto(startPlaceAndCrossway, new CoordinatesDto(44.134603, 11.122528, 1035.0),
                "", Collections.emptyList(), false);
        PlaceRefDto endPlaceMonteBaducco = new PlaceRefDto(endPlace, new CoordinatesDto(44.1389435, 11.1356351, 765.0),
                "", Collections.emptyList(), false);

        TrailImportDto trailImport345BODto = new TrailImportDto("345", "Any trail", "Any desc", 15,
                startPlaceCrocevia,
                endPlaceMonteBaducco,
                Arrays.asList(startPlaceCrocevia, endPlaceMonteBaducco), Collections.emptyList(), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(),
                "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);


        TrailResponse trail345Resp = importTrail(trailImport345BODto);
        TrailDto trail345Imported = trail345Resp.getContent().get(0);

        // Now, use the trail raw coordinates to find intersection trail

        final var trail400_4BOResp = trailRawController.getById(trail400_4Import.getContent().stream().findFirst().get().getId());
        final var trail400_4RawDto = trail400_4BOResp.getContent().get(0);

        final TrailIntersectionResponse trailIntersection =
                geoTrailController.findTrailIntersection(
                        new GeoLineDto(trail400_4RawDto.getCoordinates()
                                .stream()
                                .map(t -> new Coordinates2D(t.getLongitude(), t.getLatitude()))
                                .collect(Collectors.toList())),
                        0, 10
                );

        // Two trail intersections
        TrailIntersectionDto firstIntersectionObject = trailIntersection.getContent().get(0);
        assertThat(firstIntersectionObject.getTrail().getId())
                .isEqualTo(trail345Imported.getId());
        assertThat(firstIntersectionObject.getPoints().get(0))
                .matches(it ->
                        it.getLatitude() == 44.22511953860521 && it.getLongitude() == 10.937788942828774);

        assertThat(firstIntersectionObject.getTrail().getId())
                .isEqualTo(trail345Imported.getId());
        assertThat(firstIntersectionObject.getPoints().get(1))
                .matches(it ->
                        it.getLatitude() == 44.206141820177436 &&
                        it.getLongitude() == 10.902140624821186);
    }


    @Test
    public void onCrosswayDetection_shouldDetectTwoIntersectionsForTwoTrails() {

        TrailRawResponse trail345RawResp = trailRawController.getById(trail345Import.getContent().stream().findFirst().get().getId());
        assertThat(trail345RawResp.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = trail345RawResp.getContent().get(0);

        String startPlaceAndCrossway = "Rocca Corneta";
        String endPlace = "Abetaia";

        // 345aBO: goes from CROCEVIA -> MONTE BADUCCO
        PlaceRefDto startPlaceCrocevia = new PlaceRefDto(startPlaceAndCrossway, new CoordinatesDto(44.134603, 11.122528, 1035.0),
                "", Collections.emptyList(), false);
        PlaceRefDto endPlaceMonteBaducco = new PlaceRefDto(endPlace, new CoordinatesDto(44.1389435, 11.1356351, 765.0),
                "", Collections.emptyList(), false);

        TrailImportDto trailImport345BODto = new TrailImportDto("345", "Any trail", "Any desc", 15,
                startPlaceCrocevia,
                endPlaceMonteBaducco,
                Arrays.asList(startPlaceCrocevia, endPlaceMonteBaducco), Collections.emptyList(), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(),
                "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);


        TrailResponse trail345Resp = importTrail(trailImport345BODto);
        TrailDto trail345Imported = trail345Resp.getContent().get(0);

        // import second

        TrailRawResponse trail446RawResp = trailRawController.getById(trail446Import.getContent().stream().findFirst().get().getId());
        assertThat(trail446RawResp.getContent().size()).isEqualTo(1);
        TrailRawDto trail446RawDto = trail446RawResp.getContent().get(0);

        String startPlaceAndCrossway_2 = "Via Ronchidoso";
        String endPlace_2 = "Via Montefiore Maserno";

        // 345aBO: goes from CROCEVIA -> MONTE BADUCCO
        PlaceRefDto startPlaceCrocevia_2 = new PlaceRefDto(startPlaceAndCrossway_2, new CoordinatesDto(44.134603, 11.122528, 1035.0),
                "", Collections.emptyList(), false);
        PlaceRefDto endPlaceMonteBaducco_2 = new PlaceRefDto(endPlace_2, new CoordinatesDto(44.1389435, 11.1356351, 765.0),
                "", Collections.emptyList(), false);

        TrailImportDto trailImport446Dto = new TrailImportDto("446", "Any trail", "Any desc", 15,
                startPlaceCrocevia_2,
                endPlaceMonteBaducco_2,
                Arrays.asList(startPlaceCrocevia_2, endPlaceMonteBaducco_2), Collections.emptyList(), TrailClassification.E, "Italy",
                trail446RawDto.getCoordinates(),
                "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trail446RawDto.getFileDetails(), TrailStatus.PUBLIC);


        TrailResponse trail446Resp = importTrail(trailImport446Dto);
        TrailDto trail446Imported = trail446Resp.getContent().get(0);

        // Now, use the trail raw coordinates to find intersection trail

        final var trail400_4BOResp = trailRawController.getById(trail400_4Import.getContent().stream().findFirst().get().getId());
        final var trail400_4RawDto = trail400_4BOResp.getContent().get(0);

        final TrailIntersectionResponse trailIntersection =
                geoTrailController.findTrailIntersection(
                        new GeoLineDto(trail400_4RawDto.getCoordinates()
                                .stream()
                                .map(t -> new Coordinates2D(t.getLongitude(), t.getLatitude()))
                                .collect(Collectors.toList())),
                        0, 10
                );

        // Two trail intersections
        assertThat(trailIntersection.getContent().size()).isEqualTo(2);

        TrailIntersectionDto firstIntersectionObject = trailIntersection.getContent().get(0);
        assertThat(firstIntersectionObject.getTrail().getId())
                .isEqualTo(trail345Imported.getId());
        assertThat(firstIntersectionObject.getPoints().get(0))
                .matches(it -> it.getLatitude() == 44.22511953860521 && it.getLongitude() == 10.937788942828774);

        assertThat(firstIntersectionObject.getTrail().getId())
                .isEqualTo(trail345Imported.getId());
        assertThat(firstIntersectionObject.getPoints().get(1))
                .matches(it -> it.getLatitude() == 44.206141820177436 && it.getLongitude() == 10.902140624821186);

        TrailIntersectionDto secondIntersectionObject = trailIntersection.getContent().get(1);
        assertThat(secondIntersectionObject.getTrail().getId())
                .isEqualTo(trail446Imported.getId());
        assertThat(secondIntersectionObject.getPoints().get(0))
                .matches(it -> it.getLatitude() == 44.24680670723319 && it.getLongitude() == 10.937165915966034);

        assertThat(secondIntersectionObject.getTrail().getId())
                .isEqualTo(trail446Imported.getId());
        assertThat(secondIntersectionObject.getPoints().get(1))
                .matches(it -> it.getLatitude() == 44.21991999261081 && it.getLongitude() == 10.931598991155624);
    }

    private TrailResponse importTrail(TrailImportDto trailImportDto) {
        TrailResponse trailResponse = adminTrailController.importTrail(trailImportDto);
        assertThat(trailResponse.getContent().size()).isEqualTo(1);
        return trailResponse;
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, TrailRaw.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Place.COLLECTION_NAME);
    }
}

