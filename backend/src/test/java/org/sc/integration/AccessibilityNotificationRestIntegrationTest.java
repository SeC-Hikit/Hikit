package org.sc.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.common.rest.response.AccessibilityUnresolvedResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.AccessibilityNotificationController;
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
    public static final String EXPECTED_TRAIL_CODE = "125BO";

    public static final CoordinatesDto EXPECTED_COORDINATES = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final String ANY_SOLVED_DESC = "ANY_SOLVED_DESC";

    public static final AccessibilityNotificationCreationDto EXPECTED_AN =
            new AccessibilityNotificationCreationDto(EXPECTED_TRAIL_CODE, EXPECTED_DESCRIPTION,
                    new Date(), true, EXPECTED_COORDINATES);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccessibilityNotificationController accessibilityNotificationController;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        accessibilityNotificationController.createAccessibilityNotification(EXPECTED_AN);
    }

    @Test
    public void whenUnresolvedInDB_readUnresolvedOne() {
        AccessibilityUnresolvedResponse response = accessibilityNotificationController.getNotSolvedByTrailCode(EXPECTED_TRAIL_CODE);
        assertThat(response.getContent().size()).isEqualTo(1);
        AccessibilityUnresolvedDto firstOccurence = response.getContent().get(0);
        assertThat(firstOccurence.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(firstOccurence.getCoordinates()).isEqualTo(EXPECTED_COORDINATES);
        assertThat(firstOccurence.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(firstOccurence.getReportDate()).isEqualToIgnoringMinutes(new Date());
    }

    @Test
    public void whenUnresolved_shallSolveItAndFetchItBack() {
        AccessibilityUnresolvedResponse response = accessibilityNotificationController.getNotSolvedByTrailCode(EXPECTED_TRAIL_CODE);
        assertThat(response.getContent().size()).isEqualTo(1);
        AccessibilityUnresolvedDto firstOccurence = response.getContent().get(0);

        Date expectedResolutionDate = new Date();
        AccessibilityResponse resolvedResponse = accessibilityNotificationController.resolveNotification(
          new AccessibilityNotificationResolutionDto(firstOccurence.getId(), ANY_SOLVED_DESC, expectedResolutionDate)
        );
        assertThat(resolvedResponse.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto firstSolvedOccurence = resolvedResponse.getContent().get(0);

        assertThat(firstSolvedOccurence.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(firstSolvedOccurence.getCoordinates()).isEqualTo(EXPECTED_COORDINATES);
        assertThat(firstSolvedOccurence.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(firstSolvedOccurence.getReportDate()).isEqualToIgnoringMinutes(expectedResolutionDate);
        assertThat(firstSolvedOccurence.getResolution()).isEqualTo(ANY_SOLVED_DESC);

        AccessibilityUnresolvedResponse emptyResponse = accessibilityNotificationController.getNotSolvedByTrailCode(EXPECTED_TRAIL_CODE);
        Assert.assertTrue(emptyResponse.getContent().isEmpty());

        AccessibilityResponse accessibilityResponse = accessibilityNotificationController.getSolvedByTrailCode(EXPECTED_TRAIL_CODE);
        assertThat(accessibilityResponse.getContent().size()).isEqualTo(1);
        AccessibilityNotificationDto resolved = resolvedResponse.getContent().get(0);

        assertThat(resolved.getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
        assertThat(resolved.getCoordinates()).isEqualTo(EXPECTED_COORDINATES);
        assertThat(resolved.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
        assertThat(resolved.getReportDate()).isEqualToIgnoringMinutes(expectedResolutionDate);
        assertThat(resolved.getResolution()).isEqualTo(ANY_SOLVED_DESC);
    }

    @Test
    public void delete() {
        AccessibilityUnresolvedResponse response = accessibilityNotificationController.getNotSolvedByTrailCode(EXPECTED_TRAIL_CODE);
        assertThat(response.getContent().size()).isEqualTo(1);
        AccessibilityUnresolvedDto firstOccurence = response.getContent().get(0);

        AccessibilityResponse deleteResponse = accessibilityNotificationController.deleteAccessibilityNotification(firstOccurence.getId());
        assertThat(deleteResponse.getContent().get(0).getId()).isEqualTo(firstOccurence.getId());

        AccessibilityUnresolvedResponse emptyResponse = accessibilityNotificationController.getNotSolvedByTrailCode(EXPECTED_TRAIL_CODE);
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