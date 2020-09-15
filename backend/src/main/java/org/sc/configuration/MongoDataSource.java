package org.sc.configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.Logger;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

@Singleton
public class MongoDataSource implements DataSource {

    private final Logger LOGGER = getLogger(MongoDataSource.class);

    private static final String DATABASE_NAME_PROPERTY = "db";
    private static final String MONGO_URI_PROPERTY = "mongo-uri";
    private final String databaseName;
    private final MongoClient mongoClient;

    @Inject
    public MongoDataSource(@Named(MONGO_URI_PROPERTY) final String mongoURI,
                           @Named(DATABASE_NAME_PROPERTY) final String databaseName) {
        final MongoClientURI connectionString = new MongoClientURI(mongoURI);
        this.mongoClient = new MongoClient(connectionString);
        this.databaseName = databaseName;
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
