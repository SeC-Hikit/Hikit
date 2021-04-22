package org.sc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    public static final String APP_NAME = "Sentieri&Cartografia";
    public static final String VERSION = "1.0";
    public static final String MAJOR_VERSION = VERSION.split("\\.")[0];
    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    private final String port;
    private final String trailStorage;
    private final String tempStorage;
    private final Integer altitudeServicePort;
    private final String mongoDbUri;
    private final String dbName;
    private final String mailFrom;
    private final int resourcesCachePeriod;
    private final boolean enabledSecurity;

    @Autowired
    public AppProperties(final @Value("${server.port}") String port,
                         final @Value("${storage.path}") String storage,
                         final @Value("${temp.storage.path}") String tempStorage,
                         final @Value("${service.altitude.port}") Integer altitudeServicePort,
                         final @Value("${db.uri}") String mongoDbUri,
                         final @Value("${db.name}") String dbName,
                         final @Value("${spring.mail.from}") String mailFrom,
                         final @Value("${resources.cache.period.seconds:3600}") int resourcesCachePeriod,
                         final @Value("${security.enabled:true}") boolean enabledSecurity) {
        this.port = port;
        this.trailStorage = storage;
        this.tempStorage = tempStorage;
        this.altitudeServicePort = altitudeServicePort;
        this.mongoDbUri = mongoDbUri;
        this.dbName = dbName;
        this.mailFrom = mailFrom;
        this.resourcesCachePeriod = resourcesCachePeriod;
        this.enabledSecurity = enabledSecurity;
    }

    public String getPort() {
        return port;
    }

    public String getStorage() {
        return trailStorage;
    }

    public Integer getAltitudeServicePort() {
        return altitudeServicePort;
    }

    public String getMongoDbUri() {
        return mongoDbUri;
    }

    public String getDbName() {
        return dbName;
    }

    public String getTempStorage() {
        return tempStorage;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public int getResourcesCachePeriod() {
        return resourcesCachePeriod;
    }

    public boolean getEnabledSecurity() {
        return enabledSecurity;
    }
}