package org.sc.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.common.rest.TrailImportDto;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.AccessibilityReportController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.AccessibilityReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sc.integration.ImportTrailIT.INTERMEDIATE_COORDINATES_DTO;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccessibilityReportIntegrationTest {


    public static final String ANY_DESCRIPTION = "Report desc";
    public static final String ANY_TARGET_EMAIL = "lorenzo.verri@sentieriecartografia.it";
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
    private AccessibilityReportResponse createResponse;

    @Before
    public void setup(){
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);

        trailResponse = trailController.importTrail(trailImportDto);
        id = trailResponse.getContent().get(0).getId();
        reportDate = new Date();
        anyTrailCoord = new TrailCoordinatesDto(INTERMEDIATE_COORDINATES_DTO.getLatitude(), INTERMEDIATE_COORDINATES_DTO.getLongitude(), INTERMEDIATE_COORDINATES_DTO.getAltitude(), 5);
        createResponse = accessibilityReportController.create(new AccessibilityReportDto(null, ANY_DESCRIPTION, id, ANY_TARGET_EMAIL, ANY_TEL, "", reportDate, anyTrailCoord, null));
    }

    @Test
    public void shouldRetrieveTheNewlyInsertedReportById(){
        AccessibilityReportResponse byByTrailId = accessibilityReportController.getByTrailId(id, 0, 1);
        List<AccessibilityReportDto> content = byByTrailId.getContent();
        Assert.notEmpty(content, "Empty content value");

        AccessibilityReportDto previouslyCreated = createResponse.getContent().stream().findFirst().get();
        AccessibilityReportDto actual = content.stream().findFirst().get();

        assertThat(actual).isEqualTo(previouslyCreated);
    }

    @Test
    public void whenOnlyOneDocumentInserted_shallRetrieveItById(){
        AccessibilityReportDto previouslyCreated = createResponse.getContent().stream().findFirst().get();
        AccessibilityReportResponse byByTrailId = accessibilityReportController.getById(previouslyCreated.getId());
        List<AccessibilityReportDto> content = byByTrailId.getContent();
        Assert.notEmpty(content, "Empty content value");

        AccessibilityReportDto actual = content.stream().findFirst().get();
        assertThat(actual).isEqualTo(previouslyCreated);
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, AccessibilityReport.COLLECTION_NAME);
    }


}
