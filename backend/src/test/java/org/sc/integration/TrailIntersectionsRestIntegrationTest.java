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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailIntersectionsRestIntegrationTest {

    public static final String TRAIL_INTERSECTION_FOLDER = "intersections";
    public static final String TRAIL_001aBO_IMPORT_FILENAME = "001aBO.gpx";
    public static final String TRAIL_001BO_IMPORT_FILENAME = "001BO.gpx";

    public static final int ANY_OFFICIAL_ETA = 15;
    public static final String ANY_MAINTAINING_SECTION = "CAI Bologna";
    public static final String MAINTAINING_SECTION = "CAI Bologna";

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
    private TrailRawResponse trail001BOImport;

    private TrailDto trail001BO;
    private TrailDto trail001aBO;
    private PlaceRefDto intersectionPlaceRef;


    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail001aBOImport = importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_001aBO_IMPORT_FILENAME);
        trail001BOImport = importRawTrail(adminTrailImporterController,
                TRAIL_INTERSECTION_FOLDER + File.separator + TRAIL_001BO_IMPORT_FILENAME);
    }

    @Test
    public void shallCreateTrail_addAnotherOneWithIntersectionAndCheckDataIntegration() {

        TrailRawResponse trail001aBoResp = trailRawController.getById(trail001aBOImport.getContent().stream().findFirst().get().getId());
        assertThat(trail001aBoResp.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = trail001aBoResp.getContent().get(0);

        String startPlaceAndCrossway = "Start place (and crossway)";
        String endPlace = "End place";

        // 001aBO: goes from CROCEVIA -> MONTE BADUCCO
        PlaceRefDto startPlaceCrocevia = new PlaceRefDto(startPlaceAndCrossway, new CoordinatesDto(44.134603, 11.122528, 1035.0),
                "", Collections.emptyList(), false);
        PlaceRefDto endPlaceMonteBaducco = new PlaceRefDto(endPlace, new CoordinatesDto(44.1389435, 11.1356351, 765.0),
                "", Collections.emptyList(), false);

        TrailImportDto trailImport001aBODto = new TrailImportDto("001aBO", "Any trail", "Any desc", 15,
                startPlaceCrocevia,
                endPlaceMonteBaducco,
                Arrays.asList(startPlaceCrocevia, endPlaceMonteBaducco), Collections.emptyList(), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(),
                "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);


        TrailResponse trail001aBOResp = importTrail(trailImport001aBODto);
        TrailDto trail001aBOImported = trail001aBOResp.getContent().get(0);

        // Now, use the trail raw coordinates to find intersection trail

        TrailRawResponse trail001BOResp = trailRawController.getById(trail001BOImport.getContent().stream().findFirst().get().getId());
        TrailRawDto trail001BORawDto = trail001BOResp.getContent().get(0);

        final TrailIntersectionResponse trailIntersection =
                geoTrailController.findTrailIntersection(
                        new GeoLineDto(trail001BORawDto.getCoordinates()
                                .stream()
                                .map(t -> new Coordinates2D(t.getLongitude(), t.getLatitude()))
                                .collect(Collectors.toList())),
                        0, 10
                );

        List<TrailIntersectionDto> content = trailIntersection.getContent();
        assertThat(content.size()).isEqualTo(1);

        List<CoordinatesDto> points = content.get(0).getPoints();
        CoordinatesDto intersectionPoint = points.get(0);

        List<PlaceDto> intersectionPlaceContent = placeController.geolocatePlace(
                new PointGeolocationDto(intersectionPoint, 5), 0, 10).getContent();

        PlaceDto intersectionPlaceDto = intersectionPlaceContent.get(0);
        assertThat(intersectionPlaceDto.getCrossingTrailIds()
                .contains(trail001aBOImported.getId())).isTrue();

        intersectionPlaceRef = new PlaceRefDto(intersectionPlaceDto.getName(),
                new CoordinatesDto(intersectionPlaceDto.getCoordinates().get(0).getLatitude(),
                        intersectionPlaceDto.getCoordinates().get(0).getLongitude(),
                        intersectionPlaceDto.getCoordinates().get(0).getAltitude()),
                intersectionPlaceDto.getId(), Collections.singletonList(trail001aBOImported.getId()), false);

        // Done with intersection
        PlaceRefDto startPlaceRef2Castiglione = new PlaceRefDto("Start place 2",
                new CoordinatesDto(trail001BORawDto.getStartPos().getLatitude(), trail001BORawDto.getStartPos().getLongitude(), trail001BORawDto.getStartPos().getAltitude()),
                "", Collections.emptyList(), false);
        PlaceRefDto endPlaceRef2Balinello = new PlaceRefDto("End place 2", new CoordinatesDto(trail001BORawDto.getFinalPos().getLatitude(), trail001BORawDto.getFinalPos().getLongitude(), trail001BORawDto.getFinalPos().getAltitude()),
                "", Collections.emptyList(), false);

        // Castiglione -> Balinello di Sopra
        TrailImportDto trailImport2Dto = new TrailImportDto("001BO", "Any trail", "Any desc", ANY_OFFICIAL_ETA,
                startPlaceRef2Castiglione,
                endPlaceRef2Balinello,
                Arrays.asList(startPlaceRef2Castiglione, endPlaceRef2Balinello),
                Collections.singletonList(intersectionPlaceRef),
                TrailClassification.E, "Italy",
                trail001BORawDto.getCoordinates(),
                ANY_MAINTAINING_SECTION,
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trailImportResponse = adminTrailController.importTrail(trailImport2Dto);
        assertThat(trailImportResponse.getContent().size()).isEqualTo(1);

        TrailDto trail001BOImported = trailImportResponse.getContent().get(0);

        // Getting the placeId
        List<String> placeIds = trail001BOImported.getLocations().stream().map(PlaceRefDto::getPlaceId).collect(Collectors.toList());
        List<PlaceResponse> collect = placeIds.stream().map(placeController::get).collect(Collectors.toList());
        List<PlaceDto> crossways = collect.stream().map(PlaceResponse::getContent)
                .flatMap(Collection::stream)
                .filter(p -> p.getName().equals(startPlaceAndCrossway))
                .collect(Collectors.toList());

        assertThat(crossways.size()).isEqualTo(1);

        PlaceDto crossway = crossways.get(0);

        TrailDto firstImportedTrail = getById(trail001aBOImported.getId()).getContent().get(0);
        TrailDto secondImportedTrails = getById(trail001BOImported.getId()).getContent().get(0);

        // CROSSWAY PLACE, must contain FIRST and SECOND TRAIL IDs
        assertThat(crossway.getCrossingTrailIds().contains(firstImportedTrail.getId())).isTrue();
        assertThat(crossway.getCrossingTrailIds().contains(secondImportedTrails.getId())).isTrue();

        // SECOND TRAIL, must contain FIRST trail ID in crossway place
        final List<PlaceRefDto> mustExist2PlaceRef = secondImportedTrails.getLocations()
                .stream().filter((location) ->
                        location.getEncounteredTrailIds().contains(firstImportedTrail.getId()))
                .collect(Collectors.toList());

        assertThat(mustExist2PlaceRef.size()).isEqualTo(1);

        // FIRST TRAIL, must contain second trail ID in crossway place
        final List<PlaceRefDto> mustExistPlaceRef = firstImportedTrail.getLocations()
                .stream().filter((location) ->
                        location.getEncounteredTrailIds().contains(secondImportedTrails.getId()))
                .collect(Collectors.toList());

        // Place add with trail ID of another trail shall contain reference.
        assertThat(mustExistPlaceRef.size()).isEqualTo(1);

        this.trail001BO = secondImportedTrails;
        this.trail001aBO = firstImportedTrail;
    }

    @Test
    public void shallCreatTrailThenAddAnotherOneWithStartingPlaceMatchingCrossway() {

        TrailRawResponse trail001aBoResp = trailRawController.getById(trail001aBOImport.getContent().stream().findFirst().get().getId());
        assertThat(trail001aBoResp.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = trail001aBoResp.getContent().get(0);


        // Create first trail 001BO
        TrailRawResponse trail001BOResp = trailRawController.getById(trail001BOImport.getContent().stream().findFirst().get().getId());
        TrailRawDto trail001BORawDto = trail001BOResp.getContent().get(0);

        PlaceRefDto startPlaceRef2Castiglione = new PlaceRefDto("Start place 2",
                new CoordinatesDto(trail001BORawDto.getStartPos().getLatitude(), trail001BORawDto.getStartPos().getLongitude(), trail001BORawDto.getStartPos().getAltitude()),
                "", Collections.emptyList(), false);
        PlaceRefDto endPlaceRef2Balinello = new PlaceRefDto("End place 2", new CoordinatesDto(trail001BORawDto.getFinalPos().getLatitude(), trail001BORawDto.getFinalPos().getLongitude(), trail001BORawDto.getFinalPos().getAltitude()),
                "", Collections.emptyList(), false);

        // Castiglione -> Balinello di Sopra
        TrailImportDto trailImport2Dto = new TrailImportDto("001BO", "Any trail", "Any desc", ANY_OFFICIAL_ETA,
                startPlaceRef2Castiglione,
                endPlaceRef2Balinello,
                Arrays.asList(startPlaceRef2Castiglione, endPlaceRef2Balinello),
                Collections.emptyList(),
                TrailClassification.E, "Italy",
                trail001BORawDto.getCoordinates(),
                ANY_MAINTAINING_SECTION,
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trailImportResponse = adminTrailController.importTrail(trailImport2Dto);
        TrailDto trail001BOImported = trailImportResponse.getContent().get(0);
        assertThat(trailImportResponse.getContent().size()).isEqualTo(1);

        // 001aBO: goes from CROCEVIA -> MONTE BADUCCO
        String startPlaceAndCrossway = "Start place (and crossway)";
        String endPlace = "End place";

        PlaceRefDto startPlaceCrocevia = new PlaceRefDto(startPlaceAndCrossway, new CoordinatesDto(44.134603, 11.122528, 1035.0),
                "", Collections.singletonList(trail001BOImported.getId()), false);
        PlaceRefDto endPlaceMonteBaducco = new PlaceRefDto(endPlace, new CoordinatesDto(44.1389435, 11.1356351, 765.0),
                "", Collections.emptyList(), false);

        List<PlaceRefDto> crosswaySingleton = Collections.singletonList(startPlaceCrocevia);
            TrailImportDto trailImport001aBODto = new TrailImportDto("001aBO", "Any trail", "Any desc", 15,
                startPlaceCrocevia,
                endPlaceMonteBaducco,
                Arrays.asList(startPlaceCrocevia, endPlaceMonteBaducco),
                crosswaySingleton, TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(),
                    MAINTAINING_SECTION,
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trail001aBOResp = importTrail(trailImport001aBODto);
        TrailDto trail001aBOImported = trail001aBOResp.getContent().get(0);

        TrailDto firstImportedTrails = getById(trail001BOImported.getId()).getContent().get(0);
        TrailDto secondImportedTrail = getById(trail001aBOImported.getId()).getContent().get(0);

        assertThat(secondImportedTrail.getLocations().size()).isEqualTo(2);

        // Getting the placeId
        List<String> placeIds = firstImportedTrails.getLocations().stream().map(PlaceRefDto::getPlaceId).collect(Collectors.toList());
        List<PlaceResponse> collect = placeIds.stream().map(placeController::get).collect(Collectors.toList());
        List<PlaceDto> crossways = collect.stream().map(PlaceResponse::getContent)
                .flatMap(Collection::stream)
                .filter(p -> p.getName().equals(startPlaceAndCrossway))
                .collect(Collectors.toList());

        assertThat(crossways.size()).isEqualTo(1);
        PlaceDto crossway = crossways.get(0);

        // CROSSWAY PLACE, must contain FIRST and SECOND TRAIL IDs
        assertThat(crossway.getCrossingTrailIds().contains(secondImportedTrail.getId())).isTrue();
        assertThat(crossway.getCrossingTrailIds().contains(firstImportedTrails.getId())).isTrue();

        // FIRST TRAIL, must contain second trail ID in crossway place
        final List<PlaceRefDto> mustExist2PlaceRef = firstImportedTrails.getLocations()
                .stream().filter((location) ->
                        location.getEncounteredTrailIds().contains(secondImportedTrail.getId()))
                .collect(Collectors.toList());

        assertThat(mustExist2PlaceRef.size()).isEqualTo(1);

        // SECOND TRAIL, must contain first trail ID in crossway place
        final List<PlaceRefDto> mustExistPlaceRef = secondImportedTrail.getLocations()
                .stream().filter((location) ->
                        location.getEncounteredTrailIds().contains(firstImportedTrails.getId()))
                .collect(Collectors.toList());

        // Place add with trail ID of another trail shall contain reference.
        assertThat(mustExistPlaceRef.size()).isEqualTo(1);

        this.trail001BO = firstImportedTrails;
        this.trail001aBO = secondImportedTrail;
    }

    @Test
    public void deleteOneShallRemoveAllReferences() {
        shallCreateTrail_addAnotherOneWithIntersectionAndCheckDataIntegration();

        TrailResponse otherTrailSharingCrossway = trailController.getById(trail001BO.getId(), TrailSimplifierLevel.LOW);
        List<String> encounteredTrails = otherTrailSharingCrossway.getContent().get(0).getLocations().stream().map(PlaceRefDto::getEncounteredTrailIds)
                .flatMap(Collection::stream).collect(Collectors.toList());

        assertThat(encounteredTrails.contains(trail001aBO.getId())).isTrue();

        // Delete the first imported one
        adminTrailController.deleteById(trail001aBO.getId());

        TrailResponse otherTrailSharingCrosswayReloaded = trailController.getById(trail001BO.getId(), TrailSimplifierLevel.LOW);
        List<String> encounteredTrailsReloaded = otherTrailSharingCrosswayReloaded.getContent().get(0).getLocations().stream().map(PlaceRefDto::getEncounteredTrailIds)
                .flatMap(Collection::stream).collect(Collectors.toList());
        assertThat(encounteredTrailsReloaded.contains(trail001aBO.getId())).isFalse();

        PlaceResponse placeResponse = placeController.get(0, 1000, false, NO_FILTERING_TOKEN);
        List<String> allPlaces = placeResponse.getContent().stream().map(PlaceDto::getCrossingTrailIds).flatMap(Collection::stream).collect(Collectors.toList());

        assertThat(allPlaces.contains(trail001aBO.getId())).isFalse();
    }

    @Test
    public void onePublishedTrailShallBeSetToDraftAndBackToPublished() {
        shallCreateTrail_addAnotherOneWithIntersectionAndCheckDataIntegration();

        TrailResponse otherTrailSharingCrossway = trailController.getById(trail001BO.getId(), TrailSimplifierLevel.LOW);
        List<String> encounteredTrails = otherTrailSharingCrossway.getContent().get(0).getLocations().stream().map(PlaceRefDto::getEncounteredTrailIds)
                .flatMap(Collection::stream).collect(Collectors.toList());

        assertThat(encounteredTrails.contains(trail001aBO.getId())).isTrue();

        // Changed the status
        TrailDto trail001aBo = trailController.getById(trail001aBO.getId(), TrailSimplifierLevel.LOW).getContent().stream().findFirst().get();
        assertThat(trail001aBo.getStatus()).isEqualTo(TrailStatus.PUBLIC);
        trail001aBo.setStatus(TrailStatus.DRAFT);

        // SUT - PUBLISHED -> DRAFT
        TrailResponse trailToBeDraftedResp = adminTrailController.updateTrailStatus(trail001aBo);
        TrailDto trailToBeDrafted = trailToBeDraftedResp.getContent().get(0);
        assertThat(trailToBeDrafted.getStatus()).isEqualTo(TrailStatus.DRAFT);

        // Ensure the trail is like 'deleted'
        TrailResponse otherTrailSharingCrosswayReloaded = trailController.getById(trail001BO.getId(), TrailSimplifierLevel.LOW);
        List<String> encounteredTrailsReloaded = otherTrailSharingCrosswayReloaded.getContent().get(0).getLocations().stream().map(PlaceRefDto::getEncounteredTrailIds)
                .flatMap(Collection::stream).collect(Collectors.toList());
        assertThat(encounteredTrailsReloaded.contains(trail001aBO.getId())).isFalse();

        PlaceResponse placeResponse = placeController.get(0, 1000, false,  NO_FILTERING_TOKEN);
        List<String> allPlaces = placeResponse.getContent().stream().map(PlaceDto::getCrossingTrailIds).flatMap(Collection::stream).collect(Collectors.toList());
        assertThat(allPlaces.contains(trail001aBO.getId())).isFalse();

        // SUT - DRAFT -> PUBLISHED
        TrailDto byIdReloaded = trailController.getById(trail001aBO.getId(),
                TrailSimplifierLevel.LOW).getContent().stream().findFirst().get();
        assertThat(byIdReloaded.getStatus()).isEqualTo(TrailStatus.DRAFT);
        byIdReloaded.setStatus(TrailStatus.PUBLIC);
        adminTrailController.updateTrailStatus(byIdReloaded);

        byIdReloaded.getLocations().forEach(it ->
                {
                    final PlaceDto placeDto = placeController.get(it.getPlaceId()).getContent().stream().findFirst().get();
                    assertThat(placeDto.getCrossingTrailIds().contains(byIdReloaded.getId())).isTrue();
                }
        );
        TrailResponse trail001BOReloaded = trailController.getById(trail001BO.getId(), TrailSimplifierLevel.LOW);
        List<PlaceRefDto> encounteredIntersectionPlaceReloaded = trail001BOReloaded
                .getContent().get(0).getLocations().stream()
                .filter(t-> t.getName().equals(intersectionPlaceRef.getName())).collect(Collectors.toList());

        PlaceRefDto intersection = encounteredIntersectionPlaceReloaded.stream().findFirst().get();
        assertThat(intersection.getEncounteredTrailIds().contains((byIdReloaded.getId()))).isTrue();
    }


    private TrailResponse getById(String trailId) {
        return trailController.getById(trailId, TrailSimplifierLevel.LOW);
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

    public TrailRawResponse importRawTrail(final AdminTrailImporterController adminTrailImporterController,
                                           final String fileName) throws IOException {
        return adminTrailImporterController.importGpx(
                new MockMultipartFile("file", fileName, "multipart/form-data",
                        getClass().getClassLoader().getResourceAsStream("trails" + File.separator + fileName)
                )
        );
    }
}

