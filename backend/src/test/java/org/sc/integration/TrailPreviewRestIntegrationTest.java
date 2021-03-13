package org.sc.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.TrailImportDto;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.common.rest.response.TrailPreviewResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.TrailImporterController;
import org.sc.controller.TrailPreviewController;
import org.sc.data.model.Trail;
import org.sc.data.model.TrailClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sc.integration.ImportTrailIT.*;
import static org.sc.integration.TrailImportRestIntegrationTest.START_EXPECTED_COORDINATE;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailPreviewRestIntegrationTest {

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    public static final String EXPECTED_TRAIL_CODE = "125BO";
    private static final Date EXPECTED_DATE = new Date();
    public static final String EXPECTED_COUNTRY = "Italy";
    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;
    public static final String EXPECTED_MAINTAINANCE_SECTION = "CAI Bologna";


    public static final TrailImportDto EXPECTED_TRAIL_DTO = new TrailImportDto(EXPECTED_TRAIL_CODE, EXPECTED_NAME, EXPECTED_DESCRIPTION,
            20, TrailImportRestIntegrationTest.SINGLETON_LIST_OF_REF_PLACES,
            EXPECTED_TRAIL_CLASSIFICATION, EXPECTED_COUNTRY,
            Arrays.asList(
                    START_EXPECTED_COORDINATE, INTERMEDIATE_EXPECTED_COORDINATE, END_EXPECTED_COORDINATE
            ), EXPECTED_DATE, EXPECTED_MAINTAINANCE_SECTION, false, "Porretta", new Date());

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TrailImporterController importController;

    @Autowired
    private TrailPreviewController controller;

    @Before
    public void setUp() {
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
        importController.importTrail(EXPECTED_TRAIL_DTO);
    }

    @Test
    public void getById_shouldFindOne() {
        TrailPreviewResponse response = controller.getPreviewByCode(EXPECTED_TRAIL_CODE);
        assertThat(response.getContent().size()).isEqualTo(1);
        TrailPreviewDto firstResult = response.getContent().get(0);

        assertThat(firstResult.getDate()).isEqualToIgnoringSeconds(EXPECTED_DATE);
        assertThat(firstResult.getClassification()).isEqualTo(EXPECTED_TRAIL_CLASSIFICATION);
        assertThat(firstResult.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(firstResult.getStartPos()).isEqualTo(EXPECTED_START_POS);
        assertThat(firstResult.getFinalPos()).isEqualTo(EXPECTED_FINAL_POS);
    }

    @Test
    public void getPaged_shouldFindOne() {
        TrailPreviewResponse response = controller.getAllPreview(0, 1);
        assertThat(response.getContent().size()).isEqualTo(1);
        TrailPreviewDto firstResult = response.getContent().get(0);

        assertThat(firstResult.getDate()).isEqualToIgnoringSeconds(EXPECTED_DATE);
        assertThat(firstResult.getClassification()).isEqualTo(EXPECTED_TRAIL_CLASSIFICATION);
        assertThat(firstResult.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(firstResult.getStartPos()).isEqualTo(EXPECTED_START_POS);
        assertThat(firstResult.getFinalPos()).isEqualTo(EXPECTED_FINAL_POS);
    }

    @Test
    public void get0Paged_shouldNotFindAny() {
        TrailPreviewResponse response = controller.getPreviewByCode("123_NOT_FOUND");
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