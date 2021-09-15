package org.sc.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.AccessibilityReportController;
import org.sc.controller.admin.AdminAccessibilityReportController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.AccessibilityReport;
import org.sc.data.model.Place;
import org.sc.data.model.Trail;
import org.sc.data.repository.AccessibilityReportDao;
import org.sc.manager.AccessibilityNotificationManager;
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
    private AdminAccessibilityReportController accessibilityReportAdminController;
    @Autowired
    private AccessibilityReportDao accessibilityReportDao;

    @Autowired
    private AccessibilityNotificationManager accessibilityNotificationManager;

    @Autowired
    private AdminPlaceController placeController;
    @Autowired
    private AdminTrailController trailController;

    @Autowired AuthFacade authHelper;

    @Autowired
    private DataSource dataSource;

    private TrailResponse trailResponse;
    private String trailId;
    private Date reportDate;
    private CoordinatesDto anyTrailCoord;
    private AccessibilityReportResponse createResponse;

    @Before
    public void setup() {
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);

        trailResponse = trailController.importTrail(trailImportDto);
        trailId = trailResponse.getContent().get(0).getId();
        reportDate = new Date();
        anyTrailCoord = new CoordinatesDto(INTERMEDIATE_COORDINATES_DTO.getLatitude(), INTERMEDIATE_COORDINATES_DTO.getLongitude(), INTERMEDIATE_COORDINATES_DTO.getAltitude());
        createResponse = accessibilityReportController.create(new AccessibilityReportDto(null, ANY_DESCRIPTION, trailId, ANY_TARGET_EMAIL, ANY_TEL, "", reportDate, false, anyTrailCoord, null));
    }

    @Test
    public void shouldRetrieveTheNewlyInsertedReportById() {
        AccessibilityReportResponse byByTrailId = accessibilityReportController.getByTrailId(trailId, 0, 1);
        List<AccessibilityReportDto> content = byByTrailId.getContent();
        Assert.notEmpty(content, "Empty content value");

        AccessibilityReportDto previouslyCreated = createResponse.getContent().stream().findFirst().get();
        AccessibilityReportDto actual = content.stream().findFirst().get();

        assertThat(actual).isEqualTo(previouslyCreated);
    }

    @Test
    public void whenOnlyOneDocumentInserted_shallRetrieveItById() {
        AccessibilityReportDto previouslyCreated = createResponse.getContent().stream().findFirst().get();
        AccessibilityReportResponse byByTrailId = accessibilityReportController.getById(previouslyCreated.getId());
        List<AccessibilityReportDto> content = byByTrailId.getContent();
        Assert.notEmpty(content, "Empty content value");

        AccessibilityReportDto actual = content.stream().findFirst().get();
        assertThat(actual).isEqualTo(previouslyCreated);
    }

    @Test
    public void shallDeleteIt() {
        AccessibilityReportDto previouslyCreated = createResponse.getContent().stream().findFirst().get();
        AccessibilityReportResponse byByTrailId = accessibilityReportController.getById(previouslyCreated.getId());
        List<AccessibilityReportDto> content = byByTrailId.getContent();
        Assert.notEmpty(content, "Empty content value");

        AccessibilityReportDto expected = content.stream().findFirst().get();
        AccessibilityReportResponse accessibilityReportResponse =
                accessibilityReportAdminController.deleteAccessibilityNotificationReport(content.stream().findFirst().get().getId());
        AccessibilityReportDto actual = accessibilityReportResponse.getContent().stream().findFirst().get();
        assertThat(actual).isEqualTo(expected);

        AccessibilityReportResponse shallBeEmptyResponse = accessibilityReportController.getById(previouslyCreated.getId());
        assertThat(shallBeEmptyResponse.getContent()).asList().isEmpty();
    }

    @Test
    public void shallFindItCheckingForNotUpgraded() {
        AccessibilityReportResponse unapgraded = accessibilityReportController.getUnapgraded(authHelper.getAuthHelper().getRealm(), 0, Integer.MAX_VALUE);
        assertThat(unapgraded.getContent().stream().findFirst().get()).isEqualTo(createResponse.getContent().stream().findFirst().get());
    }

    @Test
    public void shallUpgradeIt() {
        AccessibilityReportDto previouslyCreated = createResponse.getContent().stream().findFirst().get();
        AccessibilityReportResponse byByTrailId = accessibilityReportController.getById(previouslyCreated.getId());
        List<AccessibilityReportDto> content = byByTrailId.getContent();
        AccessibilityReportDto expected = content.stream().findFirst().get();

        AccessibilityReportResponse accessibilityReportResponse =
                accessibilityReportAdminController.upgradeReport(content.stream().findFirst().get().getId());

        AccessibilityReportDto upgradedReport = accessibilityReportResponse.getContent().stream().findFirst().get();
        assertThat(upgradedReport.getId()).isEqualTo(expected.getId());

        List<AccessibilityNotificationDto> upgradedNotification = accessibilityNotificationManager.byId(upgradedReport.getIssueId());

        assertThat(upgradedNotification).isNotEmpty();
        AccessibilityNotificationDto actualUpgradedNotification = upgradedNotification.stream().findFirst().get();

        assertThat(actualUpgradedNotification.getCoordinates()).isEqualTo(expected.getCoordinates());
        assertThat(actualUpgradedNotification.getTrailId()).isEqualTo(expected.getTrailId());
        assertThat(actualUpgradedNotification.getDescription()).isEqualTo(expected.getDescription());

        AccessibilityReportResponse unapgraded = accessibilityReportController.getUnapgraded(authHelper.getAuthHelper().getRealm(), 0, Integer.MAX_VALUE);
        assertThat(unapgraded.getContent()).isEmpty();
        AccessibilityReportResponse upgraded = accessibilityReportController.getUpgraded(authHelper.getAuthHelper().getRealm(), 0, Integer.MAX_VALUE);
        assertThat(upgraded.getContent()).isNotEmpty();

        assertThat(upgraded.getContent().stream().findFirst().get()).isEqualTo(upgradedReport);
    }

    @Test
    public void validate(){

        AccessibilityReportDto createdReport = createResponse.getContent().stream().findFirst().get();

        assertThat(createdReport.getValid()).isFalse();

        String newlyCreatedReportId = createdReport.getId();
        List<AccessibilityReport> previouslyCreatedDBObject = accessibilityReportDao.getById(newlyCreatedReportId);
        AccessibilityReport newlyCreatedReport = previouslyCreatedDBObject.get(0);

        AccessibilityReportResponse validateResponse = accessibilityReportController.validate(newlyCreatedReport.getValidationId());
        assertThat(validateResponse.getContent()).isNotEmpty();

        AccessibilityReportResponse byIdResponse = accessibilityReportController.getById(newlyCreatedReportId);
        assertThat(byIdResponse.getContent().get(0).getValid()).isTrue();

    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, AccessibilityReport.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, AccessibilityNotification.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Place.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
    }


}
