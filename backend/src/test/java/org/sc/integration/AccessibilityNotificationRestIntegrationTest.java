package org.sc.integration;

import org.junit.*;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.AccessibilityNotificationController;
import org.sc.controller.admin.AdminAccessibilityIssueController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.data.model.AccessibilityNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccessibilityNotificationRestIntegrationTest {

    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";

    public static final CoordinatesDto EXPECTED_COORDINATES = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final String ANY_SOLVED_DESC = "ANY_SOLVED_DESC";

    AccessibilityNotificationDto expectedCreationDto;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccessibilityNotificationController accessibilityNotificationController;

    @Autowired
    private AdminAccessibilityIssueController adminAccessibilityIssueController;

    @Autowired
    private AdminPlaceController placeController;

    @Autowired
    private AdminTrailImporterController importController;

    @Autowired
    private AdminTrailController trailController;

    private TrailResponse trailResponse;
    private String id;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImportWithNoCrossways(placeController);
        trailResponse = trailController.importTrail(trailImportDto);
        id = trailResponse.getContent().get(0).getId();
        Date reportDate = new Date();
        adminAccessibilityIssueController.create(
                new AccessibilityNotificationDto(null, EXPECTED_DESCRIPTION, id,
                        reportDate, reportDate, true, "", EXPECTED_COORDINATES, new RecordDetailsDto()));
    }

    @Test
    public void whenUnresolvedInDB_readUnresolvedOne() {
        AccessibilityResponse response = accessibilityNotificationController.getNotSolvedByTrailId(id, 0, 1);
        assertThat(response.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto firstOccurence = response.getContent().get(0);
        assertThat(firstOccurence.getTrailId()).isEqualTo(id);
        assertThat(firstOccurence.getCoordinates()).isEqualTo(EXPECTED_COORDINATES);
        assertThat(firstOccurence.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(firstOccurence.getReportDate()).isEqualToIgnoringMinutes(new Date());
    }

    @Test
    public void whenUnresolved_shallSolveItAndFetchItBack() {
        AccessibilityResponse response = accessibilityNotificationController.getNotSolvedByTrailId(id, 0, 1);
        assertThat(response.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto firstOccurence = response.getContent().get(0);

        Date expectedResolutionDate = new Date();
        AccessibilityResponse resolvedResponse = adminAccessibilityIssueController.resolveNotification(
                new AccessibilityNotificationResolutionDto(firstOccurence.getId(), ANY_SOLVED_DESC, expectedResolutionDate)
        );
        assertThat(resolvedResponse.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto firstSolvedOccurence = resolvedResponse.getContent().get(0);

        assertThat(firstSolvedOccurence.getTrailId()).isEqualTo(id);
        assertThat(firstSolvedOccurence.getCoordinates()).isEqualTo(EXPECTED_COORDINATES);
        assertThat(firstSolvedOccurence.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(firstSolvedOccurence.getReportDate()).isEqualToIgnoringMinutes(expectedResolutionDate);
        assertThat(firstSolvedOccurence.getResolution()).isEqualTo(ANY_SOLVED_DESC);

        AccessibilityResponse emptyResponse = accessibilityNotificationController.getNotSolvedByTrailId(id, 0, 1);
        Assert.assertTrue(emptyResponse.getContent().isEmpty());

        AccessibilityResponse accessibilityResponse = accessibilityNotificationController.getSolvedByTrailId(id, 0, 1);
        assertThat(accessibilityResponse.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto resolved = resolvedResponse.getContent().get(0);

        assertThat(resolved.getTrailId()).isEqualTo(id);
        assertThat(resolved.getCoordinates()).isEqualTo(EXPECTED_COORDINATES);
        assertThat(resolved.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(resolved.getReportDate()).isEqualToIgnoringMinutes(expectedResolutionDate);
        assertThat(resolved.getResolution()).isEqualTo(ANY_SOLVED_DESC);
    }

    @Test
    public void delete() {
        AccessibilityResponse response = accessibilityNotificationController.getNotSolvedByTrailId(id, 0, 1);
        assertThat(response.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto firstOccurence = response.getContent().get(0);

        AccessibilityResponse deleteResponse = adminAccessibilityIssueController.deleteAccessibilityNotification(firstOccurence.getId());
        assertThat(deleteResponse.getContent().get(0).getId()).isEqualTo(firstOccurence.getId());

        AccessibilityResponse emptyResponse = accessibilityNotificationController.getNotSolvedByTrailId(id, 0, 1);
        Assert.assertTrue(emptyResponse.getContent().isEmpty());
    }

    @Test
    public void contextLoads() {
        assertThat(accessibilityNotificationController).isNotNull();
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, AccessibilityNotification.COLLECTION_NAME);
    }

}