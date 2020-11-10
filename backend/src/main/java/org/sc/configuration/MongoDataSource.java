package org.sc.configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.Logger;
import org.sc.common.config.DataSource;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

@Singleton
public class MongoDataSource implements DataSource {

    private final Logger LOGGER = getLogger(MongoDataSource.class);

    private final String databaseName;
    private final MongoClient mongoClient;

    @Inject
    public MongoDataSource(final AppProperties appProperties) {
        final MongoClientURI connectionString = new MongoClientURI(appProperties.getDbUri());
        this.mongoClient = new MongoClient(connectionString);
        this.databaseName = appProperties.getDbName();
        LOGGER.info(format("Going to connect to DB '%s'. Connection String '%s'", databaseName, connectionString));
    }

    public MongoClient getClient() {
        return mongoClient;
    }

    public MongoDatabase getDB() {
        return mongoClient.getDatabase(databaseName);
    }

    @Override
    public String getDBName() {
        return databaseName;
    }


}
