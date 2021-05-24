package org.sc.integration;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.GeoLineDto;
import org.sc.common.rest.geo.RectangleDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.GeoTrailController;
import org.sc.controller.TrailController;
import org.sc.controller.TrailRawController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.controller.admin.AdminTrailRawController;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RawTrailRestIntegrationTest {

    public static final String TRAIL_035_IMPORT_FILENAME = "035BO.gpx";
    public static final String TRAIL_033_IMPORT_FILENAME = "033BO.gpx";

    @Autowired DataSource dataSource;
    @Autowired TrailRawController trailRawController;
    @Autowired AdminTrailImporterController adminTrailImporterController;
    @Autowired AdminTrailRawController adminTrailRawController;
    @Autowired AdminPlaceController placeController;
    @Autowired AdminTrailController adminTrailController;
    @Autowired TrailController trailController;
    @Autowired GeoTrailController geoTrailController;


    private TrailRawResponse trail035Import;
    private TrailRawResponse trail033Import;


    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail035Import = importRawTrail(adminTrailImporterController, TRAIL_035_IMPORT_FILENAME);
        trail033Import = importRawTrail(adminTrailImporterController, TRAIL_033_IMPORT_FILENAME);
    }

    @Test
    public void shallReadRawData() {
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        assertThat(byId.getContent().size()).isEqualTo(1);
        TrailRawDto actual = byId.getContent().get(0);
        assertThat(trail035Import.getContent().get(0)).isEqualTo(actual);
    }

    @Test
    public void shallDeleteRawData() {
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        assertThat(byId.getContent().size()).isEqualTo(1);
        String id = byId.getContent().get(0).getId();
        adminTrailRawController.deleteById(id);
        TrailRawResponse actual = trailRawController.getById(id);
        Assertions.assertThat(actual.getContent()).isEmpty();
    }

    @Test
    public void shallCreateATrailFromRawAndGeolocateIt(){
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        assertThat(byId.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = byId.getContent().get(0);

        String placeId1 = "Any";
        String placeId2 = "Any2";

        String start_place = "Start place";
        PlaceResponse any_fountain = placeController.create(new PlaceDto(placeId1, start_place, "", Collections.singletonList("Any fountain"),
                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.13998529867459, 11.15928920022217, 765.0)), Collections.emptyList(),
                new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        String end_place = "End place";
        PlaceResponse another_fountain = placeController.create(new PlaceDto(placeId2, end_place, "", Collections.singletonList("Another fountain"),
                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0)), Collections.emptyList(),
                new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        PlaceRefDto startPlace = new PlaceRefDto(start_place, new TrailCoordinatesDto(44.13998529867459, 11.15928920022217, 765.0, 0),
                any_fountain.getContent().get(0).getId());
        PlaceRefDto endPlace = new PlaceRefDto(end_place, new TrailCoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0, 6345),
                another_fountain.getContent().get(0).getId());

        TrailImportDto trailImportDto = new TrailImportDto("ABC", "Any trail", "Any desc", 15,
                startPlace,
                endPlace,
                Arrays.asList(startPlace, endPlace), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(), "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trailResponse = adminTrailController.importTrail(trailImportDto);


        assertThat(trailController.getById(
                trailResponse.getContent().stream().findFirst().get().getId(), false)
                .getContent().size()).isEqualTo(1);
        trailController.getById(trailResponse.getContent().get(0).getId(), false);

        TrailResponse geoLocateTrail = trailController.geoLocateTrail(new RectangleDto(new Coordinates2D(11.15928920022217, 44.13998529867459),
                new Coordinates2D(11.156454500448556, 44.138395199458394)));

        assertThat(geoLocateTrail.getContent()).asList().isNotEmpty();
        assertThat(geoLocateTrail.getContent().get(0)).isEqualTo(trailResponse.getContent().get(0));
    }

    @Test
    public void shallCreateTwoTrailsFromRawAndFindIntersections(){
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        assertThat(byId.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = byId.getContent().get(0);

        //
        // Create the FIRST trail
        // - create places first;
        // - create the trail
        //
        String placeId1 = "Any";
        String placeId2 = "Any2";

        String start_place = "Start place";
        PlaceResponse any_fountain = placeController.create(new PlaceDto(placeId1, start_place, "", Collections.singletonList("Any fountain"),
                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.13998529867459, 11.15928920022217, 765.0)), Collections.emptyList(),
                new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        String end_place = "End place";
        PlaceResponse another_fountain = placeController.create(new PlaceDto(placeId2, end_place, "", Collections.singletonList("Another fountain"),
                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0)), Collections.emptyList(),
                new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        PlaceRefDto startPlace = new PlaceRefDto(start_place, new TrailCoordinatesDto(44.13998529867459, 11.15928920022217, 765.0, 0),
                any_fountain.getContent().get(0).getId());
        PlaceRefDto endPlace = new PlaceRefDto(end_place, new TrailCoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0, 6345),
                another_fountain.getContent().get(0).getId());

        TrailImportDto trailImportDto = new TrailImportDto("ABC", "Any trail", "Any desc", 15,
                startPlace,
                endPlace,
                Arrays.asList(startPlace, endPlace), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(), "CAI Bologna",
                false, "Ovest", Collections.emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trailResponse = adminTrailController.importTrail(trailImportDto);


        assertThat(trailController.getById(
                trailResponse.getContent().stream().findFirst().get().getId(), false)
                .getContent().size()).isEqualTo(1);
        trailController.getById(trailResponse.getContent().get(0).getId(), false);

        //
        //  Create the second trail
        //
        TrailRawResponse byId033 = trailRawController.getById(trail033Import.getContent().stream().findFirst().get().getId());
        assertThat(byId033.getContent().size()).isEqualTo(1);
        TrailRawDto importedTrail033 = byId033.getContent().get(0);

//        String placeId3 = "Any3";
//        String placeId4 = "Any4";
//
//        String startPlaceName = "Another Start place";
//        PlaceResponse any_start_waterfall = placeController.create(new PlaceDto(placeId3, startPlaceName, "", Collections.singletonList("Any waterfall"),
//                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.134854998681604, 11.130673499683706, 1118.0)), Collections.emptyList(),
//                new RecordDetailsDto(new Date(), importedTrail033.getFileDetails().getUploadedBy(), importedTrail033.getFileDetails().getOnInstance(), importedTrail033.getFileDetails().getRealm())));
//
//        String endPlaceName = "Another End place";
//        PlaceResponse any_end_bivouac = placeController.create(new PlaceDto(placeId4, endPlaceName, "", Collections.singletonList("Another fountain"),
//                Collections.emptyList(), Collections.singletonList(new CoordinatesDto(44.11522879887705, 11.159186600446347, 775.0)), Collections.emptyList(),
//                new RecordDetailsDto(new Date(), importedTrail033.getFileDetails().getUploadedBy(), importedTrail033.getFileDetails().getOnInstance(), importedTrail033.getFileDetails().getRealm())));
//
//        PlaceRefDto startPlace2 = new PlaceRefDto(startPlaceName, new TrailCoordinatesDto(44.134854998681604, 11.130673499683706, 1118.0, 0),
//                any_start_waterfall.getContent().get(0).getId());
//        PlaceRefDto endPlace2 = new PlaceRefDto(endPlaceName, new TrailCoordinatesDto(44.11522879887705, 11.159186600446347, 1035.0, 5376),
//                any_end_bivouac.getContent().get(0).getId());
//
//        TrailImportDto trail2Import = new TrailImportDto("ABC 2", "Any trail 2", "Any desc 2", 15,
//                startPlace2,
//                endPlace2,
//                Arrays.asList(startPlace, endPlace), TrailClassification.EE, "Italy",
//                importedTrail033.getCoordinates(), "CAI Bologna",
//                false, "Ovest", Collections.emptyList(),
//                new Date(), importedTrail033.getFileDetails(), TrailStatus.PUBLIC);
//
//        TrailResponse trail2Response = adminTrailController.importTrail(trail2Import);
//
//        assertThat(trailController.getById(
//                trail2Response.getContent().stream().findFirst().get().getId(), false)
//                .getContent().size()).isEqualTo(1);
//        trailController.getById(trail2Response.getContent().get(0).getId(), false);

        TrailIntersectionResponse trailIntersection = geoTrailController.findTrailIntersection(
                new GeoLineDto(importedTrail033.getCoordinates()
                        .stream().map(a -> new Coordinates2D(a.getLongitude(), a.getLatitude())).collect(Collectors.toList())),
                0, 1000);

        List<TrailIntersectionDto> content = trailIntersection.getContent();

        assertThat(content.get(0).getPoints().get(0).getLatitude()).isEqualTo(44.1278146989955);
        assertThat(content.get(0).getPoints().get(0).getLongitude()).isEqualTo(11.14361829962144);

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

