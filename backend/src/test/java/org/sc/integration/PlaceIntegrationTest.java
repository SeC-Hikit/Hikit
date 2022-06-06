package org.sc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.PlaceRefDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailImportDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.PlaceController;
import org.sc.controller.TrailController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PlaceIntegrationTest extends ImportTrailIT {

    public static final String LEVEL = TrailSimplifierLevel.FULL.toString();

    @Autowired
    private DataSource dataSource;

    @Autowired private AdminPlaceController adminPlaceController;
    @Autowired private PlaceController placeController;
    @Autowired private AdminTrailController adminTrailController;
    @Autowired private TrailController trailController;
    @Autowired private AdminTrailImporterController importerController;

    private PlaceResponse addedPlace;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        addedPlace = adminPlaceController.create(CORRECT_PLACE_DTO);
    }

    @Test
    public void shouldRetrieveItBack() {
        PlaceResponse placeResponse = placeController.get(addedPlace.getContent().get(0).getId());
        PlaceDto returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);
    }


    @Test
    public void shouldRetrieveAndModifyItBack() {
        PlaceResponse placeResponse = placeController.get(addedPlace.getContent().get(0).getId());
        PlaceDto returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        String another_magical_description = "Another magical description";
        String another_name = "Another name";

        returnedPlaceDto.setDescription(another_magical_description);
        returnedPlaceDto.setName(another_name);
        adminPlaceController.update(returnedPlaceDto);

        placeResponse = placeController.get(addedPlace.getContent().get(0).getId());
        returnedPlaceDto = placeResponse.getContent().get(0);

        assertThat(returnedPlaceDto.getDescription()).isEqualTo(another_magical_description);
        assertThat(returnedPlaceDto.getName()).isEqualTo(another_name);
    }

    @Test
    public void shouldRetrieveAndDeleteIt() {
        PlaceResponse placeResponse = placeController.get(addedPlace.getContent().get(0).getId());
        PlaceDto returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        String placeId = returnedPlaceDto.getId();

        // Import trail
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImportWithNoCrossways(adminPlaceController);
        TrailResponse importedResponse = adminTrailController.importTrail(trailImportDto);
        String trailId = importedResponse.getContent().get(0).getId();

        TrailResponse addPlaceToTrailResponse = adminTrailController.addPlaceToTrail(trailId,
                new PlaceRefDto("ANYZ", INTERMEDIATE_EXPECTED_COORDINATE, placeId, Collections.emptyList(), false));

        assertThat(addPlaceToTrailResponse.getStatus()).isEqualTo(Status.OK);
        TrailResponse trailResponse = trailController.getByPlaceId(placeId, LEVEL, 0, 10);

        assertThat(trailResponse.getContent().isEmpty()).isEqualTo(false);

        adminPlaceController.delete(placeId);

        // Removed from place collection
        placeResponse = placeController.get(placeId);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(true);

        // Check has been removed from trails too
        trailResponse = trailController.getByPlaceId(placeId, LEVEL, 0, 10);
        assertThat(trailResponse.getContent().isEmpty()).isEqualTo(true);
    }



    @Test
    public void shouldRetrieveItBackByProvidingAHint() {
        PlaceResponse placeResponse = placeController.getLikeNameOrTags("A mag", 0, 10, NO_FILTERING_TOKEN);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(false);
        PlaceDto returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        placeResponse = placeController.getLikeNameOrTags("a", 0, 10, NO_FILTERING_TOKEN);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(false);
        returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        placeResponse = placeController.getLikeNameOrTags("A", 0, 10, NO_FILTERING_TOKEN);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(false);
        returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        placeResponse = placeController.getLikeNameOrTags("B", 0, 10, NO_FILTERING_TOKEN);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(true);
    }

}
