package org.sc.frontend;

import org.sc.common.config.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class Main {

    public static final String PREFIX = "app";
    static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    final static String LOGO = "CAI Sentieri e Cartografia frontend v" + ConfigurationProperties.VERSION;

    public static void main(String[] args) {
        SpringApplication backend = new SpringApplication(Main.class);
        backend.setBannerMode(Banner.Mode.OFF);
        backend.run(args);
        LOGGER.info(LOGO);
    }
}
