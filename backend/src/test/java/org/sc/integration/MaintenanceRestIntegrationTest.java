package org.sc.integration;

import org.hikit.common.datasource.Datasource;
import org.junit.*;
import org.junit.runner.RunWith;
import org.sc.common.rest.MaintenanceDto;
import org.sc.common.rest.RecordDetailsDto;
import org.sc.common.rest.TrailImportDto;
import org.sc.common.rest.response.TrailResponse;
import org.sc.controller.MaintenanceController;
import org.sc.controller.admin.AdminMaintenanceController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.TrailClassification;
import org.sc.common.rest.response.MaintenanceResponse;
import org.sc.data.mapper.MaintenanceMapper;
import org.sc.data.repository.MaintenanceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MaintenanceRestIntegrationTest {

    private static final String EXPECTED_NAME = "ANY";
    private static final String EXPECTED_NAME_2 = "ANY_2";
    private static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    private static final String EXPECTED_TRAIL_CODE = "A101";

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

    @Autowired
    private Datasource dataSource;

    @Autowired
    private MaintenanceController maintenanceController;
    @Autowired
    private AdminMaintenanceController adminMaintenanceController;

    // Skip validation, and add past maintenance
    @Autowired
    private MaintenanceDAO maintenanceDAO;
    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Autowired
    private AdminPlaceController placeController;
    @Autowired
    private AdminTrailController adminTrailController;
    private String importedTrailId;

    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);

        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);
        TrailResponse trailResponse = adminTrailController.importTrail(trailImportDto);
        importedTrailId = trailResponse.getContent().get(0).getId();

        maintenanceDAO.upsert(maintenanceMapper.map(new MaintenanceDto(null, EXPECTED_DATE_IN_PAST(), importedTrailId, "",
                EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_NAME_2, new RecordDetailsDto())));
    }

    @Test
    public void getPast_shouldFindOne() {
        MaintenanceResponse response = maintenanceController.getPastMaintenance(0, 2, NO_FILTERING_TOKEN);
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getTrailId()).isEqualTo(importedTrailId);
    }

    @Test
    public void getFuture_shouldFindOne() {
        adminMaintenanceController.create(new MaintenanceDto(null, EXPECTED_DATE_IN_FUTURE(), importedTrailId, "",
                EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_NAME_2, new RecordDetailsDto()));

        MaintenanceResponse response = maintenanceController.getFutureMaintenance(0, 2, NO_FILTERING_TOKEN);
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getTrailId()).isEqualTo(importedTrailId);
    }

    @Test
    public void whenMaintenanceIsCreatedWithTrailCodeAndNotTrailId_shouldCreateAndGet() {
        adminMaintenanceController.create(new MaintenanceDto(null, EXPECTED_DATE_IN_FUTURE(), "", EXPECTED_TRAIL_CODE,
                EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_NAME_2, new RecordDetailsDto()));
        MaintenanceResponse response = maintenanceController.getFutureMaintenance(0, 1, NO_FILTERING_TOKEN);
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getTrailCode()).isEqualTo(EXPECTED_TRAIL_CODE);
    }

    @Test
    public void delete() {
        adminMaintenanceController.create(new MaintenanceDto(null, EXPECTED_DATE_IN_FUTURE(), importedTrailId, "",
                EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_NAME_2, new RecordDetailsDto()));

        MaintenanceResponse response = maintenanceController.getFutureMaintenance(0, 2, NO_FILTERING_TOKEN);
        String id = response.getContent().get(0).getId();

        MaintenanceResponse maintenanceResponse = adminMaintenanceController.deleteMaintenance(id);
        assertThat(maintenanceResponse.getContent().get(0).getId()).isEqualTo(id);

        MaintenanceResponse responseAfterSecondCall = maintenanceController.getFutureMaintenance(0, 2, NO_FILTERING_TOKEN);
        Assert.assertTrue(responseAfterSecondCall.getContent().isEmpty());
    }

    @Test
    public void contextLoads() {
        assertThat(adminMaintenanceController).isNotNull();
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, "core.Maintenance");
    }

}