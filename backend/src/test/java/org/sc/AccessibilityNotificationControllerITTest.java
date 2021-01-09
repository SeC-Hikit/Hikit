package org.sc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.controller.AccessibilityNotificationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccessibilityNotificationControllerITTest {

    @Autowired
    private AccessibilityNotificationController controller;

    @Test
    public void contextLoads(){
        assertThat(controller).isNotNull();
    }
}