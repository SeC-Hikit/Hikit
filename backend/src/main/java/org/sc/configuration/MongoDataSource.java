package org.sc.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.Logger;
import org.sc.common.config.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class MongoDataSource implements DataSource {

    private final Logger LOGGER = getLogger(MongoDataSource.class);

    private final String databaseName;
    private final MongoClient mongoClient;

    @Autowired
    public MongoDataSource(final AppProperties appProperties) {
        this.mongoClient = MongoClients.create(appProperties.getMongoDbUri());
        this.databaseName = appProperties.getDbName();
        LOGGER.info(format("Going to connect to DB '%s'. Connection String '%s'",
                databaseName, appProperties.getMongoDbUri()));
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
