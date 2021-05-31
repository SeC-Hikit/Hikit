package org.sc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.RectangleDto;
import org.sc.common.rest.response.PlaceResponse;
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
import org.sc.data.model.Coordinates2D;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sc.integration.TrailManipulationRestIntegrationTest.TRAIL_033_IMPORT_FILENAME;
import static org.sc.integration.TrailManipulationRestIntegrationTest.TRAIL_035_IMPORT_FILENAME;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GeoTrailRestIntegrationTest {

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

    private TrailRawResponse trail035Import;
    private TrailRawResponse trail033Import;

    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail035Import = importRawTrail(adminTrailImporterController, TRAIL_035_IMPORT_FILENAME);
        trail033Import = importRawTrail(adminTrailImporterController, TRAIL_033_IMPORT_FILENAME);
    }

    @Test
    public void afterImportingTrail_shallFindItUsingGeoTrailCall() {
        createAndAssertTrail();
        TrailResponse trailResponse = geoTrailController.geoLocateTrail(new RectangleDto(
                new Coordinates2D(11.15928920022217, 44.13998529867459),
                new Coordinates2D(11.156454500448556, 44.138395199458394)));

        assertThat(trailResponse.getContent()).asList().isNotEmpty();
    }

    // TODO: one test shall not find it
    // TODO: one test shall find two
    // TODO: one test shall pass too large rectangle to be processed


    private void createAndAssertTrail() {
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        assertThat(byId.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = byId.getContent().get(0);

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
                any_fountain.getContent().get(0).getId());
        PlaceRefDto endPlace = new PlaceRefDto(end_place, new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0),
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
