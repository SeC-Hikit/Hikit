package org.sc.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.Logger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Component
public class MongoDataSource implements DataSource {

    private static final Logger LOGGER = getLogger(MongoDataSource.class);

    private final String databaseName;
    private final MongoClient mongoClient;

    @Autowired
    public MongoDataSource(final AppProperties appProperties) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings.builder().codecRegistry(pojoCodecRegistry)
                .applyConnectionString(
                new ConnectionString(appProperties.getMongoDbUri()));
        this.mongoClient = MongoClients.create();
        this.databaseName = appProperties.getDbName();
        LOGGER.info(format("Setting connection to DB '%s'. Connection String: '%s'",
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
