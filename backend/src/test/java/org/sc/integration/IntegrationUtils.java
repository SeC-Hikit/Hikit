package org.sc.integration;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.hikit.common.datasource.Datasource;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.data.model.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

public class IntegrationUtils {

    static void emptyCollection(final Datasource dataSource,
                                final String collectionName){
        MongoCollection<Document> collection =
                dataSource.getDB().getCollection(collectionName);
        collection.deleteMany(new Document());
    }

    static void clearCollections(Datasource dataSource) {
        IntegrationUtils.emptyCollection(dataSource, Trail.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Media.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Maintenance.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, AccessibilityNotification.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, AccessibilityReport.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Poi.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, Place.COLLECTION_NAME);
        IntegrationUtils.emptyCollection(dataSource, "core.TrailDatasetVersion");
    }

    public static TrailRawResponse importRawTrail(final AdminTrailImporterController adminTrailImporterController,
                                                  final String fileName,
                                                  final Class caller) throws IOException {
        return adminTrailImporterController.importGpx(
                new MockMultipartFile("file", fileName, "multipart/form-data",
                        caller.getClassLoader().getResourceAsStream("trails" + File.separator + fileName)
                )
        );
    }
}
