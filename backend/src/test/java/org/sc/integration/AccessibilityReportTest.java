package org.sc.integration;

import org.junit.After;
import org.junit.Before;
import org.sc.common.rest.*;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.AccessibilityReportController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.AccessibilityReport;
import org.sc.data.model.TrailCoordinates;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.sc.integration.ImportTrailIT.INTERMEDIATE_COORDINATES_DTO;

public class AccessibilityReportTest {


    public static final String ANY_DESCRIPTION = "Report desc";
    public static final String ANY_TARGET_EMAIL = "test-receiver@sentieriecartografia.it";
    public static final String ANY_TEL = "0123456789";


    @Autowired
    private AccessibilityReportController accessibilityReportController;
    @Autowired
    private AdminPlaceController placeController;
    @Autowired
    private AdminTrailController trailController;

    @Autowired
    private DataSource dataSource;

    private TrailResponse trailResponse;
    private String id;
    private Date reportDate;
    private TrailCoordinatesDto anyTrailCoord;

    @Before
    public void setup(){
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);

        trailResponse = trailController.importTrail(trailImportDto);
        id = trailResponse.getContent().get(0).getId();
        reportDate = new Date();
        anyTrailCoord = new TrailCoordinatesDto(INTERMEDIATE_COORDINATES_DTO.getLatitude(), INTERMEDIATE_COORDINATES_DTO.getLongitude(), INTERMEDIATE_COORDINATES_DTO.getAltitude(), 5);
        accessibilityReportController.create(new AccessibilityReportDto(null, ANY_DESCRIPTION, id, ANY_TARGET_EMAIL, ANY_TEL, "", reportDate, anyTrailCoord, null));
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, AccessibilityReport.COLLECTION_NAME);
    }


}
