package org.sc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.admin.AdminMaintenanceController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.CycloClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TrailExportIntegrationTest {


    public static final String ANY_LONG_DESC =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
                    "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                    "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    @Autowired
    private AdminPlaceController placeController;
    @Autowired
    private AdminTrailController trailController;
    @Autowired
    private AdminMaintenanceController adminMaintenanceController;


    @Autowired
    AuthFacade authHelper;

    @Autowired
    private DataSource dataSource;

    private TrailResponse trailResponse;

    @Before
    public void setup() {
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);
        trailResponse = trailController.importTrail(trailImportDto);
    }

    @Test
    public void shallGeneratePdfWithCycloDescription() {
        TrailDto trailDto = trailResponse.getContent().stream().findFirst().get();
        trailDto.setCycloDetails(new CycloDetailsDto(CycloClassification.BC_PLUS, 120, new CycloFeasibilityDto(true, 10),
                new CycloFeasibilityDto(false, 0), ANY_LONG_DESC));
        adminMaintenanceController.create(new MaintenanceDto(null, new Date(), trailDto.getId(), "San Lazzaro", "", "Mario Rossi", new RecordDetailsDto()));
        trailController.updateTrail(trailDto);
    }


}
