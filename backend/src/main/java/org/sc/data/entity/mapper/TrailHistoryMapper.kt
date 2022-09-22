package org.sc.data.entity.mapper

import org.bson.Document
import org.sc.data.model.TrailHistory
import org.springframework.beans.factory.annotation.Autowired

class TrailHistoryMapper @Autowired constructor(private val trailMapper: TrailMapper) : Mapper<TrailHistory> {

    override fun mapToObject(document: Document) =
        TrailHistory(
                document.getString(TrailHistory.ID),
                trailMapper.mapToObject(
                        document.get(TrailHistory.DATA, Document::class.java)),
                document.getDate(TrailHistory.INSERTED))

    override fun mapToDocument(trailHistoryObject: TrailHistory): Document =
            Document().append(TrailHistory.ID, trailHistoryObject.id)
                    .append(TrailHistory.DATA, trailMapper.mapToDocument(trailHistoryObject.data))
                    .append(TrailHistory.INSERTED, trailHistoryObject.inserted)
}