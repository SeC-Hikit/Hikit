package org.sc.data.repository

import org.bson.types.ObjectId
import org.sc.configuration.DataSource
import org.sc.data.entity.mapper.TrailHistoryMapper
import org.sc.data.model.Trail
import org.sc.data.model.TrailHistory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TrailHistoryDAO @Autowired constructor(private val dataSource: DataSource,
                                             private val trailHistoryMapper: TrailHistoryMapper) {
    private val collection = dataSource.db.getCollection("");

    fun create(trail: Trail) {
        val trailHistory = TrailHistory(ObjectId().toHexString(), trail, Date())
        val trailHistoryDocument = trailHistoryMapper.mapToDocument(trailHistory)
        collection.insertOne(trailHistoryDocument)
    }
}