package org.sc.frontend.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

public class ConfiguratorModule extends AbstractModule {

    private static final String DEFAULT_PROPERTY_FILE_NAME = "app.properties";
    private final Logger LOGGER = getLogger(ConfiguratorModule.class);

    private static final String APP_NAME = "Sentieri&Cartografia frontend";
    private static final String FILE_PATH_OPTION = "p";


    final String pathToProperties;


    public ConfiguratorModule(String[] args) {
        Options options = new Options();
        options.addOption(FILE_PATH_OPTION, true, "Properties file");

        final CommandLineParser cmdLineParser = new DefaultParser();
        try {
            final CommandLine cmd = cmdLineParser.parse(options, args);
            pathToProperties = cmd.getOptionValue(FILE_PATH_OPTION);
        } catch (ParseException e) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APP_NAME, options);
            throw new RuntimeException("Wrongly formatted arguments being passed");
        }
    }

    @Override
    protected void configure() {
        final Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(getPropertyStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Names.bindProperties(binder(), properties);
    }

    private InputStream getPropertyStream() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final File givenPropertiesPath = new File(pathToProperties);
            LOGGER.info(format("Using properties at passed via argument '%s'", pathToProperties));
            return new FileInputStream(givenPropertiesPath);
        } catch (final Exception e) {
            LOGGER.info("Defaulting properties");
            return contextClassLoader.getResourceAsStream(DEFAULT_PROPERTY_FILE_NAME);
        }
    }
}
