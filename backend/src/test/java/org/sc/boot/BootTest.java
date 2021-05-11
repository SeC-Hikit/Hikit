package org.sc.boot;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.controller.TrailController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class BootTest {

    @Autowired
    TrailController trailController;

    @Test
    public void shouldCheckForDI() {
        Assert.assertNotNull(trailController);
    }

}
