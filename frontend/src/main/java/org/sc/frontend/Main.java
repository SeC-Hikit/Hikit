package org.sc.frontend;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.BasicConfigurator;
import org.sc.common.config.ConfigurationProperties;
import org.sc.frontend.configuration.ConfigurationManager;
import org.sc.frontend.configuration.ConfiguratorModule;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Main {

    final static String LOGO = "CAI Sentieri e Cartografia frontend v" + ConfigurationProperties.VERSION;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        getLogger(Main.class).info(LOGO);
        /*
         * Guice.createInjector() takes your Modules, and returns a new Injector
         * instance. Most applications will call this method exactly once, in their
         * main() method.
         */
        final Injector injector = Guice.createInjector(new ConfiguratorModule(args));

        /*
         * Now that we've got the injector, we can build objects.
         */
        final ConfigurationManager configurationManager = injector.getInstance(ConfigurationManager.class);
        configurationManager.init();

    }
}
