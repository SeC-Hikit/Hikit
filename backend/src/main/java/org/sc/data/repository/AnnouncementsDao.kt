package org.sc.data.repository

import com.mongodb.client.MongoCollection
import org.apache.logging.log4j.LogManager
import org.bson.Document
import org.sc.configuration.DataSource
import org.sc.data.entity.mapper.AnnouncementMapper
import org.sc.data.model.Announcement
import org.sc.data.model.RecordDetails
import org.sc.data.repository.MongoUtils.DESCENDING_ORDER
import org.sc.data.repository.MongoUtils.DOT
import org.springframework.stereotype.Repository

@Repository
class AnnouncementsDao(dataSource: DataSource,
                       private val announcementsMapper: AnnouncementMapper) {

    private val logger = LogManager.getLogger(AnnouncementsDao::class.java)

    val collection: MongoCollection<Document>

    init {
        collection = dataSource.getDB().getCollection(Announcement.COLLECTION_NAME)
    }

    fun get(skip: Int,
            limit: Int,
            realm: String): List<Announcement> {
        val realmFilter = MongoUtils.getConditionalEqFilter(realm, Announcement.RECORD_DETAILS + DOT + RecordDetails.REALM)
        return toAnnouncementList(collection.find(realmFilter)
                .skip(skip)
                .limit(limit)
                .sort(Document(Announcement.RECORD_DETAILS + DOT + RecordDetails.UPLOADED_ON,
                        DESCENDING_ORDER)))
    }

    private fun toAnnouncementList(documents: Iterable<Document>): List<Announcement> {
        return documents.map { t: Document -> announcementsMapper.mapToObject(t) }
    }

}