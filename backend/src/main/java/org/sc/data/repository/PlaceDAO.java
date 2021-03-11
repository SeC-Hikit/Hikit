package org.sc.data.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.*;
import org.sc.data.model.Trail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceDAO {
    private final MongoCollection<Document> collection;
    private final PlaceMapper placeMapper;

    @Autowired
    public PlaceDAO(final DataSource dataSource,
                    final PlaceMapper placeMapper) {
        this.collection = dataSource.getDB().getCollection(Trail.COLLECTION_NAME);
        this.placeMapper = placeMapper;
    }


}
