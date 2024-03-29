package org.sc;

import org.sc.configuration.AppProperties;
import org.sc.configuration.StartupChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(
        exclude = {MongoAutoConfiguration.class},
        scanBasePackages={"org.hikit.common", "org.sc"}
)
public class Main {

    final static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    final static String LOGO = "\n-------------------------\n" +
            AppProperties.APP_NAME + " v" + AppProperties.DISPLAYED_VERSION + "\n" +
            "-------------------------";

    final StartupChecker startupChecker;

    public Main(StartupChecker startupChecker) {
        this.startupChecker = startupChecker;
    }

    public static void main(String[] args) {
        SpringApplication backend = new SpringApplication(Main.class);
        backend.setBannerMode(Banner.Mode.OFF);
        backend.run(args);
        LOGGER.info(LOGO);
    }
}

