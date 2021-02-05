package org.sc.integration;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.sc.configuration.DataSource;

public class IntegrationUtils {
    static void emptyCollection(final DataSource dataSource,
                                final String collectionName){
        MongoCollection<Document> collection =
                dataSource.getDB().getCollection(collectionName);
        collection.deleteMany(new Document());
    }
}
