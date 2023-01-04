package org.sc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class AppProperties {

    public static final String APP_NAME = "Sentieri&Cartografia";
    public static final String DISPLAYED_VERSION = "1.4.0";
    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    private final String port;
    private final String trailStorage;
    private final String tempStorage;
    private final String altitudeServiceHost;
    private final Integer altitudeServicePort;
    private final String mongoDbUri;
    private final String dbName;
    private final String mailFrom;
    private final int resourcesCachePeriod;
    private final boolean enabledSecurity;
    private final String securityDisabledUserRoles;
    private final String instanceId;
    private final String instanceRealm;
    private final String instanceHostname;
    private final String validationAddress;
    private final int jobImageBatchSize;
    private final double jobCrosswayConsistencyDistance;

    @Autowired
    public AppProperties(final @Value("${server.port}") String port,
                         final @Value("${storage.path}") String storage,
                         final @Value("${temp.storage.path}") String tempStorage,
                         final @Value("${service.altitude.host:127.0.0.1}") String altitudeServiceHost,
                         final @Value("${service.altitude.port}") Integer altitudeServicePort,
                         final @Value("${db.uri}") String mongoDbUri,
                         final @Value("${db.name}") String dbName,
                         final @Value("${spring.mail.from}") String mailFrom,
                         final @Value("${resources.cache.period.seconds:3600}") int resourcesCachePeriod,
                         final @Value("${security.enabled:true}") boolean enabledSecurity,
                         final @Value("${security.disabled.user-roles}") String secDisabledUserRoles,
                         final @Value("${instance.id}") String instanceId,
                         final @Value("${instance.realm}") String instanceRealm,
                         final @Value("${instance.hostname:127.0.0.1}") String instanceHostname,
                         final @Value("${instance.report.validation.address}") String validationAddress,
                         final @Value("${jobImage.batchsize}") int jobImageBatchSize,
                         final @Value("${job.crossway.consistency.distance:100.0}") double jobCrosswayConsistencyDistance
    ) {
        this.port = port;
        this.trailStorage = storage;
        this.tempStorage = tempStorage;
        this.altitudeServiceHost = altitudeServiceHost;
        this.altitudeServicePort = altitudeServicePort;
        this.mongoDbUri = mongoDbUri;
        this.dbName = dbName;
        this.mailFrom = mailFrom;
        this.resourcesCachePeriod = resourcesCachePeriod;
        this.enabledSecurity = enabledSecurity;
        this.securityDisabledUserRoles = secDisabledUserRoles;
        this.instanceId = instanceId;
        this.instanceRealm = instanceRealm;
        this.instanceHostname = instanceHostname;
        this.validationAddress = validationAddress;
        this.jobImageBatchSize = jobImageBatchSize;
        this.jobCrosswayConsistencyDistance = jobCrosswayConsistencyDistance;
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

    public boolean getIsSecurityEnabled() {
        return enabledSecurity;
    }

    public String getSecurityDisabledUserRoles() {
        return securityDisabledUserRoles;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInstanceRealm() {
        return instanceRealm;
    }

    public String getInstanceHostname() {
        return instanceHostname;
    }

    public String getValidationAddress() {
        return validationAddress;
    }

    public String getAltitudeServiceHost() {
        return altitudeServiceHost;
    }

    public int getJobImageBatchSize() {
        return jobImageBatchSize;
    }

    public double getJobCrosswayConsistencyDistance() {
        return jobCrosswayConsistencyDistance;
    }
}