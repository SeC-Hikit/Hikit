package org.sc.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailPreviewRestIntegrationTest {

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_NAME_2 = "ANY_2";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    public static final String EXPECTED_TRAIL_CODE = "125BO";
    private static final Date EXPECTED_DATE = new Date();
    public static final List<String> EXPECTED_TAGS = Arrays.asList("one", "two");
    public static final List<String> EXPECTED_TAGS_2 = Arrays.asList("three", "four");
    public static final String EXPECTED_COUNTRY = "Italy";
    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;
    public static final String EXPECTED_MAINTAINANCE_SECTION = "CAI Bologna";

    // Start POS coordinates
    public static final TrailCoordinatesDto START_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436084, 11.315620, 250.0, 0);

    public static final TrailCoordinatesDto INTERMEDIATE_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.436084, 11.315620, 250.0, 0);

    // End Pos coordinates
    public static final TrailCoordinatesDto END_EXPECTED_COORDINATE = new TrailCoordinatesDto(44.568191623, 11.154781567, 250.0, 50);
    public static final PlaceDto EXPECTED_START_POS = new PlaceDto(EXPECTED_NAME, EXPECTED_TAGS, START_EXPECTED_COORDINATE, Collections.emptyList());
    public static final PlaceDto EXPECTED_FINAL_POS = new PlaceDto(EXPECTED_NAME_2, EXPECTED_TAGS_2, END_EXPECTED_COORDINATE, Collections.emptyList());
    public static final TrailImportDto EXPECTED_TRAIL_DTO = new TrailImportDto(EXPECTED_TRAIL_CODE, EXPECTED_NAME, EXPECTED_DESCRIPTION,
            EXPECTED_START_POS,
            EXPECTED_FINAL_POS,
            20, Collections.singletonList(new PlaceDto(EXPECTED_NAME, EXPECTED_TAGS, INTERMEDIATE_EXPECTED_COORDINATE, Collections.emptyList())),
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
    public void setUp(){
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
        importController.importTrail(EXPECTED_TRAIL_DTO);
    }

    @Test
    public void getById_shouldFindOne(){
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
    public void getPaged_shouldFindOne(){
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
    public void get0Paged_shouldNotFindAny(){
        TrailPreviewResponse response = controller.getPreviewByCode("123_NOT_FOUND");
        Assert.assertTrue(response.getContent().isEmpty());
    }

    @Test
    public void contextLoads(){
        assertThat(controller).isNotNull();
    }

    @After
    public void setDown(){
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
    }

}