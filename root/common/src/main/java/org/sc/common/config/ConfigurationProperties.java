package org.sc.common.config;

public class ConfigurationProperties {
    public static final String VERSION = "1.0";
    public static final String MAJOR_VERSION = VERSION.split("\\.")[0];
    public static final String API_PREFIX = "api/v" + MAJOR_VERSION;

    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    public static final String ACCEPT_TYPE = "application/json";
}
