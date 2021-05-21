package org.sc.integration;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.TrailRawDto;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.TrailRawController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.controller.admin.AdminTrailRawController;
import org.sc.data.model.TrailRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RawTrailRestIntegrationTest {

    public static final String TRAIL_035_IMPORT_FILENAME = "035BO.gpx";
    public static final String TRAIL_033_IMPORT_FILENAME = "033BO.gpx";

    @Autowired
    DataSource dataSource;
    @Autowired
    TrailRawController trailRawController;
    @Autowired
    AdminTrailImporterController adminTrailImporterController;
    @Autowired
    AdminTrailRawController adminTrailRawController;

    private TrailRawResponse trail035Import;
    private TrailRawResponse trail033Import;


    @Before
    public void setUp() throws IOException {
        IntegrationUtils.clearCollections(dataSource);
        trail035Import = importRawTrail(adminTrailImporterController, TRAIL_035_IMPORT_FILENAME);
        trail033Import = importRawTrail(adminTrailImporterController, TRAIL_033_IMPORT_FILENAME);
    }

    @Test
    public void shallReadRawData() {
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        Assertions.assertThat(byId.getContent().size()).isEqualTo(1);
        TrailRawDto actual = byId.getContent().get(0);
        Assertions.assertThat(trail035Import.getContent().get(0)).isEqualTo(actual);
    }

    @Test
    public void shallDeleteRawData() {
        TrailRawResponse byId = trailRawController.getById(trail035Import.getContent().stream().findFirst().get().getId());
        Assertions.assertThat(byId.getContent().size()).isEqualTo(1);
        String id = byId.getContent().get(0).getId();
        adminTrailRawController.deleteById(id);
        TrailRawResponse actual = trailRawController.getById(id);
        Assertions.assertThat(actual.getContent()).isEmpty();
    }


    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, TrailRaw.COLLECTION_NAME);
    }

    public TrailRawResponse importRawTrail(final AdminTrailImporterController adminTrailImporterController,
                                           final String fileName) throws IOException {
        return adminTrailImporterController.importGpx(
                new MockMultipartFile("file", fileName, "multipart/form-data",
                        getClass().getClassLoader().getResourceAsStream("trails" + File.separator + fileName)
                )
        );
    }
}

