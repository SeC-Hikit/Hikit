package org.sc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.PlaceController;
import org.sc.data.model.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PlaceIntegrationTest extends ImportTrailIT {

    private static final String EXPECTED_DESCRIPTION = "<p>ANY_DESCRIPTION</p>";
    public static final List<String> TAGS = Arrays.asList("Magic", "Place");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlaceController controller;

    private PlaceResponse addedPlace;

    @Before
    public void setUp(){
        IntegrationUtils.clearCollections(dataSource);
        addedPlace = controller.add(CORRECT_PLACE_DTO);
    }

    @Test
    public void shouldRetrieveItBack(){
        PlaceResponse placeResponse = controller.get(addedPlace.getContent().get(0).getId());
        PlaceDto returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);
    }


}
