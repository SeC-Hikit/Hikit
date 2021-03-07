package org.sc.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.MaintenanceCreationDto;
import org.sc.data.model.Maintenance;
import org.sc.data.model.TrailClassification;
import org.sc.common.rest.response.MaintenanceResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.MaintenanceController;
import org.sc.data.dto.MaintenanceMapper;
import org.sc.data.repository.MaintenanceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MaintenanceRestIntegrationTest {

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_NAME_2 = "ANY_2";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    public static final String EXPECTED_TRAIL_CODE = "125BO";
    public static final String EXPECTED_TRAIL_CODE_FUTURE = "126BO";

    private static Date EXPECTED_DATE_IN_FUTURE() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 15);
        return c.getTime();
    }

    private static Date EXPECTED_DATE_IN_PAST() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -2);
        return c.getTime();
    }

    public static final TrailClassification EXPECTED_TRAIL_CLASSIFICATION = TrailClassification.E;

    public static final MaintenanceCreationDto EXPECTED_MAINTENANCE =
            new MaintenanceCreationDto(EXPECTED_DATE_IN_FUTURE(), EXPECTED_TRAIL_CODE_FUTURE,
                    EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_NAME_2);

    public static final MaintenanceCreationDto EXPECTED_MAINTENANCE_PAST =
            new MaintenanceCreationDto(EXPECTED_DATE_IN_PAST(), EXPECTED_TRAIL_CODE,
                    EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_NAME_2);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MaintenanceController maintenanceController;

    // Skip validation, and add past maintenance
    @Autowired
    private MaintenanceDAO maintenanceDAO;
    @Autowired
    private MaintenanceMapper maintenanceMapper;


    @Before
    public void setUp() {
        IntegrationUtils.emptyCollection(dataSource, Maintenance.COLLECTION_NAME);
        maintenanceController.create(EXPECTED_MAINTENANCE);
        maintenanceDAO.upsert(maintenanceMapper.map(EXPECTED_MAINTENANCE_PAST));
    }

    @Test
    public void getPast_shouldFindOne() {
        MaintenanceResponse response = maintenanceController.getPastMaintenance(0, 2);
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getCode()).isEqualTo(EXPECTED_TRAIL_CODE);
    }

    @Test
    public void getFuture_shouldFindOne() {
        MaintenanceResponse response = maintenanceController.getFutureMaintenance(0, 2);
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getCode()).isEqualTo(EXPECTED_TRAIL_CODE_FUTURE);
    }

    @Test
    public void delete() {
        MaintenanceResponse response = maintenanceController.getFutureMaintenance(0, 2);
        String id = response.getContent().get(0).getId();

        MaintenanceResponse maintenanceResponse = maintenanceController.deleteMaintenance(id);
        assertThat(maintenanceResponse.getContent().get(0).getId()).isEqualTo(id);

        MaintenanceResponse responseAfterSecondCall = maintenanceController.getFutureMaintenance(0, 2);
        Assert.assertTrue(responseAfterSecondCall.getContent().isEmpty());
    }

    @Test
    public void contextLoads() {
        assertThat(maintenanceController).isNotNull();
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, Maintenance.COLLECTION_NAME);
    }

}