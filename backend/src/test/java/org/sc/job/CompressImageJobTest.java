package org.sc.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CompressImageJobTest {

    private static final String PATH_FILENAME = "path/filename.jpg";
    private static final String EXPECTED_PATH_FILENAME = "path/filename_m.jpg";

    @Autowired
    private CompressImageJob sut;

    @Test
    public void generateCompressedFileUrlTest() {
        assertThat(sut.generateCompressedFileUrl(PATH_FILENAME, "_m")).isEqualTo(EXPECTED_PATH_FILENAME);
    }

}