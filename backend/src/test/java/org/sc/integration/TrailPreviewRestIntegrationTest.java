package org.sc.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.TrailImportDto;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.common.rest.response.TrailPreviewResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.TrailController;
import org.sc.controller.TrailPreviewController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.data.model.Trail;
import org.sc.data.model.TrailClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sc.integration.TrailImportRestIntegrationTest.LOCATION_REFS;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailPreviewRestIntegrationTest {

    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;
    public static final String ANY_REALM = "S&C";

    @Autowired
    private DataSource dataSource;

    @Autowired
    AdminTrailImporterController importController;
    @Autowired
    TrailPreviewController controller;
    @Autowired AdminPlaceController placeController;
    @Autowired TrailController trailController;
    @Autowired AdminTrailController adminTrailController;

    private TrailResponse trailResponse;
    private String trailId;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImportWithNoCrossways(placeController);
        trailResponse = adminTrailController.importTrail(trailImportDto);
        trailId = trailResponse.getContent().get(0).getId();
    }

    @Test
    public void getById_shouldFindOne() {
        TrailPreviewResponse response = controller.getPreviewById(trailId);
        assertThat(response.getContent().size()).isEqualTo(1);
        TrailPreviewDto firstResult = response.getContent().get(0);
        assertAll(firstResult);
    }

    private void assertAll(TrailPreviewDto firstResult) {
        assertThat(firstResult.getClassification()).isEqualTo(EXPECTED_TRAIL_CLASSIFICATION);
        assertThat(firstResult.getCode()).isEqualTo(TrailImportRestIntegrationTest.EXPECTED_TRAIL_ID);
        assertThat(firstResult.getStartPos().getPlaceId()).isEqualTo(LOCATION_REFS.get(0).getPlaceId());
        assertThat(firstResult.getFinalPos().getPlaceId()).isEqualTo(LOCATION_REFS.get(2).getPlaceId());
    }

    @Test
    public void getPaged_shouldFindOne() {
        TrailPreviewResponse response = controller
                .getTrailPreviews(0, 1, ANY_REALM, true);
        assertThat(response.getContent().size()).isEqualTo(1);
        TrailPreviewDto firstResult = response.getContent().get(0);
        assertAll(firstResult);
    }

    @Test
    public void get0Paged_shouldNotFindAny() {
        TrailPreviewResponse response = controller.getPreviewById("123_NOT_FOUND");
        Assert.assertTrue(response.getContent().isEmpty());
    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
    }

}