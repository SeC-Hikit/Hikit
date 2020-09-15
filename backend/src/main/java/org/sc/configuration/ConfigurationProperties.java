package org.sc.configuration;

public class ConfigurationProperties {
    public static final String VERSION = "1.0";
    public static final String MAJOR_VERSION = VERSION.split("\\.")[0];
    public static final String API_PREFIX = "api/v" + MAJOR_VERSION;

    // Common properties TODO
    public static final String ALTITUDE_SERVICE_PROPERTY = "altitude-service-port";
    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    public static final String ACCEPT_TYPE = "application/json";
}
