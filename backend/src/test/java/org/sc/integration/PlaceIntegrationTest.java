package org.sc.integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.admin.TrailImporterController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PlaceIntegrationTest extends ImportTrailIT {

    private static final String EXPECTED_DESCRIPTION = "<p>ANY_DESCRIPTION</p>";
    public static final List<String> TAGS = Arrays.asList("Magic", "Place");

    @Autowired
    private DataSource dataSource;

    @Autowired private AdminPlaceController placeController;
    @Autowired private AdminTrailController trailController;
    @Autowired private TrailImporterController importerController;

    private PlaceResponse addedPlace;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        addedPlace = placeController.create(CORRECT_PLACE_DTO);
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
        placeController.update(returnedPlaceDto);

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
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createTrailImport(placeController);
        TrailResponse importedResponse = trailController.importTrail(trailImportDto);
        String trailId = importedResponse.getContent().get(0).getId();

        TrailResponse addPlaceToTrailResponse = trailController.addPlaceToTrail(trailId,
                new PlaceRefDto("ANYZ", INTERMEDIATE_EXPECTED_COORDINATE, placeId));

        assertThat(addPlaceToTrailResponse.getStatus()).isEqualTo(Status.OK);
        TrailResponse trailResponse = trailController.getByPlaceId(placeId, false, 0, 10);

        assertThat(trailResponse.getContent().isEmpty()).isEqualTo(false);

        placeController.delete(placeId);

        // Removed from place collection
        placeResponse = placeController.get(placeId);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(true);

        // Check has been removed from trails too
        trailResponse = trailController.getByPlaceId(placeId, false, 0, 10);
        assertThat(trailResponse.getContent().isEmpty()).isEqualTo(true);
    }



    @Test
    public void shouldRetrieveItBackByProvidingAHint() {
        PlaceResponse placeResponse = placeController.getLikeNameOrTags("A mag", 0, 10);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(false);
        PlaceDto returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        placeResponse = placeController.getLikeNameOrTags("a", 0, 10);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(false);
        returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        placeResponse = placeController.getLikeNameOrTags("A", 0, 10);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(false);
        returnedPlaceDto = placeResponse.getContent().get(0);
        assertThat(addedPlace.getContent().get(0)).isEqualTo(returnedPlaceDto);

        placeResponse = placeController.getLikeNameOrTags("B", 0, 10);
        assertThat(placeResponse.getContent().isEmpty()).isEqualTo(true);
    }

}
