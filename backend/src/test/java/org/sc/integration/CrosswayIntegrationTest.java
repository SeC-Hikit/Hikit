package org.sc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.PlaceDto;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CrosswayIntegrationTest extends ImportTrailIT {

    public static final String LEVEL = TrailSimplifierLevel.FULL.toString();
    public static final int TWELVE_SECONDS = 12000;

    @Autowired
    private DataSource dataSource;

    @Autowired private AdminPlaceController adminPlaceController;
    @Autowired private PlaceController placeController;
    @Autowired private AdminTrailController adminTrailController;
    @Autowired private TrailController trailController;

    private PlaceResponse addedPlace;
    private TrailResponse importedTrail;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        importedTrail = adminTrailController.importTrail(TrailImportRestIntegrationTest.createThreePointsTrailImportWithNoCrossways(adminPlaceController));

        addedPlace = adminPlaceController.create(getPlaceWithCrosswayTrail(Arrays.asList(
                importedTrail.getContent().stream().findFirst().get().getId()
        )));
    }

    @Test
    public void shouldCheckOneAutoUpdateCrosswayNameWithOneTrailCrossingPlace() {
        PlaceResponse placeResponse = placeController.get(addedPlace.getContent().get(0).getId());
        final PlaceDto result = placeResponse.getContent().stream().findFirst().get();

        assertThat(result.getName()).isEqualTo(PLACE_NAME);
    }


    private PlaceDto getPlaceWithCrosswayTrail(List<String> trailIdsCrossingPlace) {
        return new PlaceDto(null, PLACE_NAME, PLACE_EXPECTED_DESCRIPTION,
                TAGS, Collections.emptyList(),
                Collections.singletonList(INTERMEDIATE_COORDINATES_DTO), trailIdsCrossingPlace,
                true, null);
    }
}
