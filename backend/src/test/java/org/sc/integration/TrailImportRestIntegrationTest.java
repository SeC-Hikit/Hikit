package org.sc.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.TrailController;
import org.sc.controller.TrailImporterController;
import org.sc.data.entity.Trail;
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
public class TrailImportRestIntegrationTest {

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_NAME_2 = "ANY_2";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    public static final String EXPECTED_TRAIL_CODE = "123BO";
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
    public static final List<TrailCoordinatesDto> EXPECTED_TRAIL_COORDINATES = Arrays.asList(
            START_EXPECTED_COORDINATE, INTERMEDIATE_EXPECTED_COORDINATE, END_EXPECTED_COORDINATE
    );

    public static final TrailImportDto EXPECTED_TRAIL_DTO = new TrailImportDto(EXPECTED_TRAIL_CODE, EXPECTED_NAME, EXPECTED_DESCRIPTION,
            new PositionDto(EXPECTED_NAME, EXPECTED_TAGS, START_EXPECTED_COORDINATE),
            new PositionDto(EXPECTED_NAME_2, EXPECTED_TAGS_2, END_EXPECTED_COORDINATE),
            Collections.singletonList(new PositionDto(EXPECTED_NAME, EXPECTED_TAGS, INTERMEDIATE_EXPECTED_COORDINATE)),
            EXPECTED_TRAIL_CLASSIFICATION, EXPECTED_COUNTRY,
            EXPECTED_TRAIL_COORDINATES, EXPECTED_DATE, EXPECTED_MAINTAINANCE_SECTION);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TrailImporterController importController;

    @Autowired
    private TrailController controller;

    @Before
    public void setUp(){
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
        importController.importTrail(EXPECTED_TRAIL_DTO);
    }

    @Test
    public void getById_shouldFindOne(){
        TrailResponse getTrail = controller.getByCode(EXPECTED_TRAIL_CODE, false);
        TrailDto firstElement = getTrail.getContent().get(0);
        assertThat(getTrail.getContent().size()).isEqualTo(1);
        assertFirtElement(firstElement);
    }

    @Test
    public void getPaged_shouldFindOne(){
        TrailResponse getTrail = controller.get(0, 0, false);
        TrailDto firstElement = getTrail.getContent().get(0);
        assertThat(getTrail.getContent().size()).isEqualTo(1);
        assertFirtElement(firstElement);
    }

    @Test
    public void delete() {
        TrailResponse deletedByCode = controller.deleteByCode(EXPECTED_TRAIL_CODE, false);
        assertThat(deletedByCode.getContent().get(0).getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        TrailResponse getTrail = controller.getByCode(EXPECTED_TRAIL_CODE, false);
        Assert.assertTrue(getTrail.getContent().isEmpty());
    }

    @Test
    public void contextLoads(){
        assertThat(controller).isNotNull();
    }

    @After
    public void setDown(){
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
    }

    private void assertFirtElement(TrailDto firstElement) {
        assertThat(firstElement.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(firstElement.getCoordinates()).isEqualTo(EXPECTED_TRAIL_COORDINATES);
        assertThat(firstElement.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(firstElement.getCountry()).isEqualTo(EXPECTED_COUNTRY);
        assertThat(firstElement.getLastUpdate()).isEqualToIgnoringMinutes(EXPECTED_DATE);
        assertThat(firstElement.getClassification()).isEqualTo(EXPECTED_TRAIL_CLASSIFICATION);

    }

}