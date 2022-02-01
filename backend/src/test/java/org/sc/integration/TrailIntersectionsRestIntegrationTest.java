package org.sc.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.GeoLineDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.GeoTrailController;
import org.sc.controller.PlaceController;
import org.sc.controller.TrailController;
import org.sc.controller.TrailRawController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.controller.admin.AdminTrailRawController;
import org.sc.data.model.*;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailIntersectionsRestIntegrationTest {

    public static final String TRAIL_INTERSECTION_FOLDER = "intersections";
    public static final String TRAIL_001aBO_IMPORT_FILENAME = "001aBO.gpx";
    public static final String TRAIL_001BO_IMPORT_FILENAME = "001BO.gpx";

    public static final int ANY_OFFICIAL_ETA = 15;
    public static final String CROCEVIA_NAME = "crocevia";

    @Autowired
    DataSource dataSource;
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

    private TrailRawResponse trail001aBOImport;
    private TrailRawResponse trail001BO;


    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail001aBOImport = importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_001aBO_IMPORT_FILENAME);
        trail001BO = importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_001BO_IMPORT_FILENAME);
    }

    @Test
    public void shallCreateTrail_addAnotherOneWithIntersectionAndCheckDataIntegration() {

        TrailRawResponse trail001aBoResp = trailRawController.getById(trail001aBOImport.getContent().stream().findFirst().get().getId());
        assertThat(trail001aBoResp.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = trail001aBoResp.getContent().get(0);

        String placeId1 = "Any";
        String placeId2 = "Any2";

        String start_place = "Start place";
        PlaceResponse any_fountain = adminPlaceController.create(new PlaceDto(placeId1, start_place, "", Collections.singletonList("Any fountain"),
                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.13998529867459, 11.15928920022217, 765.0)), Collections.emptyList(),
                new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        String end_place = "End place";
        PlaceResponse another_fountain = adminPlaceController.create(new PlaceDto(placeId2, end_place, "", Collections.singletonList("Another fountain"),
                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0)), Collections.emptyList(),
                new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        PlaceRefDto startPlace = new PlaceRefDto(start_place, new CoordinatesDto(44.13998529867459, 11.15928920022217, 765.0),
                any_fountain.getContent().get(0).getId(), Collections.emptyList());
        PlaceRefDto endPlace = new PlaceRefDto(end_place, new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0),
                another_fountain.getContent().get(0).getId(), Collections.emptyList());

        TrailImportDto trailImport001aBODto = new TrailImportDto("001aBO", "Any trail", "Any desc", 15,
                startPlace,
                endPlace,
                Arrays.asList(startPlace, endPlace), Collections.emptyList(), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(),
                "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);


        TrailResponse trail001BOResp = ensureImport(trailImport001aBODto);
        TrailDto trail001BO = trail001BOResp.getContent().get(0);


        // Find intersection
        final TrailIntersectionResponse trailIntersection =
                geoTrailController.findTrailIntersection(
                        new GeoLineDto(trail001BO.getCoordinates()
                                .stream()
                                .map(t -> new Coordinates2D(t.getLongitude(), t.getLatitude())).collect(Collectors.toList())),
                        0, 10
                );

        List<TrailIntersectionDto> content = trailIntersection.getContent();
        assertThat(content.size()).isEqualTo(1);

        List<CoordinatesDto> points = content.get(0).getPoints();
        CoordinatesDto intersectionPoint = points.get(0);

        PlaceRefDto intersectionPlace = new PlaceRefDto(CROCEVIA_NAME,
                new CoordinatesDto(intersectionPoint.getLatitude(),
                        intersectionPoint.getLongitude()),
                null, Collections.singletonList(trail001BO.getId()));


        TrailCoordinatesDto firstCoordIn001BOImport = trail001BO.getCoordinates().get(0);
        TrailCoordinatesDto finalCoordIn001BOImport = trail001BO.getCoordinates().get(trail001BO.getCoordinates().size() - 1);

        PlaceRefDto startPlaceRef2 = new PlaceRefDto("any_other_place_to_start", new CoordinatesDto(firstCoordIn001BOImport.getLatitude(), firstCoordIn001BOImport.getLongitude()),
                null, Collections.emptyList());
        PlaceRefDto endPlaceRef2 = new PlaceRefDto("any_other_place_to_end", new CoordinatesDto(finalCoordIn001BOImport.getLatitude(), finalCoordIn001BOImport.getLongitude(),
                1035.0),
                null, Collections.emptyList());

        TrailImportDto trailImport2Dto = new TrailImportDto("001BO", "Any trail", "Any desc", ANY_OFFICIAL_ETA,
                startPlaceRef2,
                endPlaceRef2,
                Arrays.asList(startPlace, intersectionPlace, endPlace), Collections.emptyList(), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(),
                "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trailImportResponse = adminTrailController.importTrail(trailImport2Dto);
        assertThat(trailImportResponse.getContent().size()).isEqualTo(1);

        TrailDto trail001aBO = trailImportResponse.getContent().get(0);

        // Getting the placeId
        List<String> placeIds = trail001aBO.getLocations().stream().map(PlaceRefDto::getPlaceId).collect(Collectors.toList());
        List<PlaceResponse> collect = placeIds.stream().map(placeController::get).collect(Collectors.toList());
        List<PlaceDto> crossways = collect.stream().map(PlaceResponse::getContent)
                .flatMap(Collection::stream)
                .filter(p -> p.getName().equals(CROCEVIA_NAME))
                .collect(Collectors.toList());

        assertThat(crossways.size()).isEqualTo(1);

        PlaceDto crossway = crossways.get(0);

        TrailDto firstImportedTrail = getById(trail001BO.getId()).getContent().get(0);
        TrailDto secondImportedTrails = getById(trail001aBO.getId()).getContent().get(0);

        // Place must contain trail ID
        assertThat(crossway.getCrossingTrailIds().contains(firstImportedTrail.getId())).isTrue();
        assertThat(crossway.getCrossingTrailIds().contains(secondImportedTrails.getId())).isTrue();

        final List<PlaceRefDto> mustExistPlaceRef = firstImportedTrail.getLocations()
                .stream().filter((location) ->
                        location.getEncounteredTrailIds().contains(crossway.getId()))
                .collect(Collectors.toList());

        // Place add with trail ID of another trail shall contain reference.
        assertThat(mustExistPlaceRef.size()).isEqualTo(1);

        final List<PlaceRefDto> mustExist2PlaceRef = secondImportedTrails.getLocations()
                .stream().filter((location) ->
                        location.getEncounteredTrailIds().contains(crossway.getId()))
                .collect(Collectors.toList());

        assertThat(mustExist2PlaceRef.size()).isEqualTo(1);

    }

    private TrailResponse getById(String trailId) {
        return trailController.getById(trailId, TrailSimplifierLevel.LOW);
    }

    private TrailResponse ensureImport(TrailImportDto trailImportDto) {
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

    public TrailRawResponse importRawTrail(final AdminTrailImporterController adminTrailImporterController,
                                           final String fileName) throws IOException {
        return adminTrailImporterController.importGpx(
                new MockMultipartFile("file", fileName, "multipart/form-data",
                        getClass().getClassLoader().getResourceAsStream("trails" + File.separator + fileName)
                )
        );
    }
}

