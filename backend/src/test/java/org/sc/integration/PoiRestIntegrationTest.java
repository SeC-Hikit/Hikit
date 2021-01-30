package org.sc.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.sc.common.rest.CoordinatesDto;
import org.sc.common.rest.PoiDto;
import org.sc.common.rest.PoiMacroType;
import org.sc.controller.POIController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PoiRestIntegrationTest {

    public static final String EXPECTED_ID = "MY_ID";
    public static final String EXPECTED_NAME = "ANY_POI";
    public static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";

    @Autowired
    private POIController controller;

    @Before
    public void setUp(){
        controller.upsertPoi(new PoiDto(EXPECTED_ID, EXPECTED_NAME, EXPECTED_DESCRIPTION,
                Arrays.asList("poiType", "poiType2"), PoiMacroType.BELVEDERE,
                Arrays.asList("minorType1", "minorType2"),
                Collections.singletonList("12"), Collections.singletonList("123BO"),
                new CoordinatesDto(44.436084, 11.315620, 250.0), new Date(), new Date(),
                Arrays.asList("http://externalresource.com", "http://externalresource2.com")));
    }

    @Test
    public void contextLoads(){
        assertThat(controller).isNotNull();
    }

    @After
    public void unset(){
        controller.deletePoi(EXPECTED_ID);
    }

}