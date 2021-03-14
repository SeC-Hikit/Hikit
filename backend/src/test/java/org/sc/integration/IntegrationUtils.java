package org.sc.integration;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.sc.configuration.DataSource;
import org.sc.data.model.*;

public class IntegrationUtils {
    static void emptyCollection(final DataSource dataSource,
                                final String collectionName){
        MongoCollection<Document> collection =
                dataSource.getDB().getCollection(collectionName);
        collection.deleteMany(new Document());
    }

    static void clearCollections(DataSource dataSource) {
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Media.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Maintenance.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, AccessibilityNotification.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Poi.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Place.COLLECTION_NAME);
    }

}
