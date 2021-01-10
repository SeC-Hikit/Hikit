package org.sc.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.controller.POIController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PoiCrudIntegrationTest {

    @Autowired
    private POIController controller;

    @Test
    public void contextLoads(){
        assertThat(controller).isNotNull();
    }

}