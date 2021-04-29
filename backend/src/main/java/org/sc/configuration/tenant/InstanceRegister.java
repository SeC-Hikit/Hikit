package org.sc.configuration.tenant;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.sc.configuration.DataSource;
import org.sc.data.tenant.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InstanceRegister {

    private final MongoCollection<Document> collection;

    @Autowired
    public InstanceRegister(final DataSource dataSource) {
        collection = dataSource.getDB().getCollection(Instance.COLLECTION_NAME);
    }

    public void register(final String id,
                         final String name,
                         final String hostname) {
        collection.updateOne(new Document(Instance.ID, id),
                new Document("$set",
                        new Document(Instance.NAME, name)
                                .append(Instance.HOSTNAME, hostname)
                                .append(Instance.BOOT_TIME, new Date())),
                new UpdateOptions().upsert(true));
    }
}
