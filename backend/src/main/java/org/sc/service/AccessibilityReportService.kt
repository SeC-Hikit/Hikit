package org.sc.service

import org.sc.adapter.mail.impl.AccessibilityReportMailAdapter
import org.sc.common.rest.AccessibilityReportDto
import org.sc.manager.AccessibilityReportManager
import org.sc.manager.TrailManager
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AccessibilityReportService @Autowired constructor(
        private val accessibilityReportManager: AccessibilityReportManager,
        private val trailManager: TrailManager,
        private val accessibilityReportMailAdapter: AccessibilityReportMailAdapter) {

    fun byId(id: String): List<AccessibilityReportDto> =
            accessibilityReportManager.byId(id)

    fun getUnapgradedByRealm(realm: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportManager.getUnapgradedByRealm(realm, skip, limit)

    fun getUpgradedByRealm(realm: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportManager.getUpgradedByRealm(realm, skip, limit)

    fun getByTrailId(trailId: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportManager.getByTrailId(trailId, skip, limit)


    fun create(accessibilityNotificationCreation: AccessibilityReportDto): List<AccessibilityReportDto> {
        val trail = trailManager.getById(accessibilityNotificationCreation.trailId,
                TrailSimplifierLevel.LOW)
        val create = accessibilityReportManager.create(accessibilityNotificationCreation,
                trail.first().fileDetails.onInstance,
                trail.first().fileDetails.realm)
        val createdValue = create.first()
        accessibilityReportMailAdapter.sendValidation(createdValue.reportDate, createdValue.trailId, createdValue.recordDetails.realm)
        return create
    }

    fun update(accReport: AccessibilityReportDto): List<AccessibilityReportDto> =
        accessibilityReportManager.update(accReport)

    fun validate(validationId: String): List<AccessibilityReportDto> {
        return accessibilityReportManager.validate(validationId)
    }

    fun delete(id: String): List<AccessibilityReportDto> = accessibilityReportManager.delete(id)
    fun count(): Long = accessibilityReportManager.count()
    fun count(realm: String): Long = accessibilityReportManager.count(realm)
    fun countUpgraded(realm: String): Long = accessibilityReportManager.countUpgraded(realm)
    fun countByTrailId(id: String): Long = accessibilityReportManager.countByTrailId(id)
    fun countUnapgraded(realm: String): Long = accessibilityReportManager.countUnapgraded(realm)

}