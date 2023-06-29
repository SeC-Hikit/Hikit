package org.sc.integration;

import org.hikit.common.datasource.Datasource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.LocateDto;
import org.sc.common.rest.geo.RectangleDto;
import org.sc.common.rest.response.PlaceResponse;
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
import org.sc.data.model.Coordinates2D;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;
import org.sc.data.validator.RectangleValidator;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sc.integration.TrailManipulationRestIntegrationTest.TRAIL_033_IMPORT_FILENAME;
import static org.sc.integration.TrailManipulationRestIntegrationTest.TRAIL_035_IMPORT_FILENAME;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GeoTrailRestIntegrationTest {

    public static final TrailSimplifierLevel LEVEL = TrailSimplifierLevel.FULL;

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

    private TrailRawResponse trail035Import;
    private TrailRawResponse trail033Import;
    private String importedId;
    private String secondTrailId;

    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail035Import = ImportTrailIT.importRawTrail(adminTrailImporterController, TRAIL_035_IMPORT_FILENAME, this.getClass());
        trail033Import = ImportTrailIT.importRawTrail(adminTrailImporterController, TRAIL_033_IMPORT_FILENAME, this.getClass());
    }

    @Test
    public void afterImportingTrail_shallFindItUsingGeoTrailCall() {
        createAndAssertTrail();
        TrailResponse trailResponse = geoTrailController.geoLocateTrail(
                new LocateDto(new RectangleDto(
                        new Coordinates2D(11.15928920022217, 44.13998529867459),
                        new Coordinates2D(11.156454500448556, 44.138395199458394)),
                        emptyList()), LEVEL, true);

        assertThat(trailResponse.getContent()).asList().isNotEmpty();
        assertThat(trailResponse.getContent().get(0).getId()).isEqualTo(importedId);
    }

    @Test
    public void afterImportingTrail_shallNotFindItUsingGeoTrail_asOutOfSearchArea() {
        createAndAssertTrail();
        TrailResponse trailResponse = geoTrailController.geoLocateTrail(
                new LocateDto(new RectangleDto(
                        new Coordinates2D(11.074260, 44.513351),
                        new Coordinates2D(11.184882, 44.589685)),
                        emptyList()), LEVEL, true);

        assertThat(trailResponse.getContent()).asList().isEmpty();
    }

    @Test
    public void afterImportingTrail_shallFindItUsingGeoTrail_asPartIsWithinSearchArea() {
        createAndAssertTrail();
        TrailResponse trailResponse = geoTrailController.geoLocateTrail(
                new LocateDto(new RectangleDto(
                        new Coordinates2D(11.154664, 44.138261),
                        new Coordinates2D(11.160095, 44.139004)),
                        emptyList()),
                LEVEL, true);

        assertThat(trailResponse.getContent()).asList().isNotEmpty();
        assertThat(trailResponse.getContent().get(0).getId()).isEqualTo(importedId);
    }

    @Test
    public void afterImportingTrails_shallFindThemUsingGeoTrail_asWithinSearchArea() {
        createAndAssertTrail();
        createSecondTrailAndAssertIt();

        TrailResponse trailResponse = geoTrailController.geoLocateTrail(
                new LocateDto(new RectangleDto(
                        new Coordinates2D(11.12780418, 44.13472887),
                        new Coordinates2D(11.14989416, 44.12174993)), emptyList()),
                LEVEL, true);

        assertThat(trailResponse.getContent()).asList().isNotEmpty();
        assertThat(trailResponse.getContent()).asList().size().isEqualTo(2);
        assertThat(trailResponse.getContent().stream().map(TrailDto::getId).collect(Collectors.toList()))
                .asList().contains(importedId, secondTrailId);
    }

    @Test
    public void afterImportingTrails_shallGetTooLargeRectangleError() {
        createAndAssertTrail();
        createSecondTrailAndAssertIt();

        TrailResponse trailResponse = geoTrailController.geoLocateTrail(new LocateDto(
                new RectangleDto(
                        new Coordinates2D(10.75034053, 44.28359988),
                        new Coordinates2D(12.04720300, 44.58065428)),
                emptyList()), LEVEL, true);

        assertThat(trailResponse.getContent()).asList().isEmpty();
        assertThat(trailResponse.getStatus()).isEqualTo(Status.ERROR);
        assertThat(trailResponse.getMessages().contains(RectangleValidator.diagonalLengthError)).isTrue();
    }


    private void createAndAssertTrail() {
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        assertThat(byId.getContent().size()).isEqualTo(1);
        TrailRawDto trailRawDto = byId.getContent().get(0);

        String placeId1 = "Any";
        String placeId2 = "Any2";

        String start_place = "Start place";
        PlaceResponse any_fountain = adminPlaceController.create(new PlaceDto(placeId1, start_place, "", singletonList("Any fountain"),
                emptyList(), singletonList(new CoordinatesDto(44.13998529867459, 11.15928920022217, 765.0)), emptyList(),
                false, new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        String end_place = "End place";
        PlaceResponse another_fountain = adminPlaceController.create(new PlaceDto(placeId2, end_place, "", singletonList("Another fountain"),
                emptyList(), singletonList(new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0)), emptyList(),
                false, new RecordDetailsDto(new Date(), trailRawDto.getFileDetails().getUploadedBy(), trailRawDto.getFileDetails().getOnInstance(), trailRawDto.getFileDetails().getRealm())));

        PlaceRefDto startPlace = new PlaceRefDto(start_place, new CoordinatesDto(44.13998529867459, 11.15928920022217, 765.0),
                any_fountain.getContent().get(0).getId(), emptyList(), false);
        PlaceRefDto endPlace = new PlaceRefDto(end_place, new CoordinatesDto(44.12684089895337, 11.13139950018985, 1035.0),
                another_fountain.getContent().get(0).getId(), emptyList(), false);

        TrailImportDto trailImportDto = new TrailImportDto("ABC", "Any trail", "Any desc", 15,
                startPlace,
                endPlace,
                Arrays.asList(startPlace, endPlace), emptyList(), TrailClassification.E, "Italy",
                trailRawDto.getCoordinates(), "CAI Bologna",
                false, "Ovest", emptyList(),
                new Date(), trailRawDto.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trailResponse = adminTrailController.importTrail(trailImportDto);


        assertThat(trailController.getById(
                        trailResponse.getContent().stream().findFirst().get().getId(), LEVEL)
                .getContent().size()).isEqualTo(1);
        importedId = trailResponse.getContent().get(0).getId();
        trailController.getById(importedId, LEVEL);
    }


    private void createSecondTrailAndAssertIt() {
        String placeId3 = "Any3";
        String placeId4 = "Any4";

        TrailRawResponse byId033 = trailRawController.getById(trail033Import.getContent().stream().findFirst().get().getId());
        assertThat(byId033.getContent().size()).isEqualTo(1);
        TrailRawDto importedTrail033 = byId033.getContent().get(0);

        String startPlaceName = "Another Start place";
        PlaceResponse any_start_waterfall = adminPlaceController.create(new PlaceDto(placeId3, startPlaceName, "", singletonList("Any waterfall"),
                emptyList(), singletonList(new CoordinatesDto(44.134854998681604, 11.130673499683706, 1118.0)), emptyList(),
                false, new RecordDetailsDto(new Date(), importedTrail033.getFileDetails().getUploadedBy(), importedTrail033.getFileDetails().getOnInstance(), importedTrail033.getFileDetails().getRealm())));

        String endPlaceName = "Another End place";
        PlaceResponse any_end_bivouac = adminPlaceController.create(new PlaceDto(placeId4, endPlaceName, "", singletonList("Another fountain"),
                emptyList(), singletonList(new CoordinatesDto(44.11522879887705, 11.159186600446347, 775.0)), emptyList(),
                false, new RecordDetailsDto(new Date(), importedTrail033.getFileDetails().getUploadedBy(), importedTrail033.getFileDetails().getOnInstance(), importedTrail033.getFileDetails().getRealm())));

        PlaceRefDto startPlace2 = new PlaceRefDto(startPlaceName, new CoordinatesDto(44.134854998681604, 11.130673499683706, 1118.0),
                any_start_waterfall.getContent().get(0).getId(), emptyList(), false);
        PlaceRefDto endPlace2 = new PlaceRefDto(endPlaceName, new CoordinatesDto(44.11522879887705, 11.159186600446347, 1035.0),
                any_end_bivouac.getContent().get(0).getId(), emptyList(), false);

        TrailImportDto trail2Import = new TrailImportDto("ABC 2", "Any trail 2", "Any desc 2", 15,
                startPlace2,
                endPlace2,
                Arrays.asList(startPlace2, endPlace2), emptyList(), TrailClassification.EE, "Italy",
                importedTrail033.getCoordinates(), "CAI Bologna",
                false, "Ovest", emptyList(),
                new Date(), importedTrail033.getFileDetails(), TrailStatus.PUBLIC);

        TrailResponse trail2Response = adminTrailController.importTrail(trail2Import);

        assertThat(trailController.getById(
                        trail2Response.getContent().stream().findFirst().get().getId(), LEVEL)
                .getContent().size()).isEqualTo(1);
        secondTrailId = trail2Response.getContent().get(0).getId();
    }


}
