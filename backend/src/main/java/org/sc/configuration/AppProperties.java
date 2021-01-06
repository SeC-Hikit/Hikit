package org.sc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    public static final String VERSION = "1.0";
    public static final String MAJOR_VERSION = VERSION.split("\\.")[0];
    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    private final String port;
    private final String trailStorage;
    private final Integer altitudeServicePort;
    private final String mongoDbUri;
    private final String dbName;

    @Autowired
    public AppProperties(final @Value("${server.port}") String port,
                         final @Value("${trail.storage.path}") String trailStorage,
                         final @Value("${service.altitude.port}") Integer altitudeServicePort,
                         final @Value("${db.uri}") String mongoDbUri,
                         final @Value("${db.name}") String dbName) {
        this.port = port;
        this.trailStorage = trailStorage;
        this.altitudeServicePort = altitudeServicePort;
        this.mongoDbUri = mongoDbUri;
        this.dbName = dbName;
    }

    public String getPort() {
        return port;
    }

    public String getTrailStorage() {
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
}