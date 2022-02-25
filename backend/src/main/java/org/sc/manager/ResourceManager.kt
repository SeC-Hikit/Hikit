package org.sc.manager

import org.sc.configuration.AppProperties
import org.sc.data.model.ResourceEntry
import org.sc.data.repository.ResourceDao
import org.sc.manager.regeneration.RegenerationActionType
import org.sc.manager.regeneration.RegenerationEntryType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class ResourceManager constructor(private val resourceDao: ResourceDao,
                                  private val appProperties: AppProperties) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun addEntry(targetTrailId: String, entryCausingRegeneration: RegenerationEntryType,
                 entryId: String, user: String, action: RegenerationActionType): List<ResourceEntry> {
        return resourceDao.insert(ResourceEntry("", appProperties.instanceId,
                entryCausingRegeneration.name, entryId, targetTrailId, action.name, Date(), user))
    }

    fun getTrailEntries(): List<ResourceEntry> {
        val byInstanceId = resourceDao.getByInstanceId(appProperties.instanceId);
        byInstanceId.forEach { logger.trace("Found to process: $it") }
        return byInstanceId
    }

    fun deleteEntries(entries: List<ResourceEntry>) = entries.forEach { resourceDao.delete(it.id) }
}