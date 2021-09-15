package org.sc.service

import org.sc.adapter.mail.impl.AccessibilityReportMailAdapter
import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.AccessibilityReportDto
import org.sc.manager.AccessibilityNotificationManager
import org.sc.manager.AccessibilityReportManager
import org.sc.manager.TrailManager
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityReportService @Autowired constructor(
        private val accessibilityReportManager: AccessibilityReportManager,
        private val trailManager: TrailManager,
        private val accessibilityNotificationManager: AccessibilityNotificationManager,
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
        accessibilityNotificationCreation.valid = false // ensure it is not valid on creation
        val create = accessibilityReportManager.create(accessibilityNotificationCreation,
                trail.first().fileDetails.onInstance,
                trail.first().fileDetails.realm)
        val createdValue = create.first()
        val activationId = accessibilityReportManager.getActivationIdById(createdValue.id).first()
        accessibilityReportMailAdapter.sendValidation(createdValue.reportDate,
                trail.first().code,
                createdValue.trailId,
                createdValue.recordDetails.realm,
                createdValue.description, activationId, createdValue.email)
        return create
    }

    fun update(accReport: AccessibilityReportDto): List<AccessibilityReportDto> =
            accessibilityReportManager.update(accReport)

    fun validate(validationId: String): List<AccessibilityReportDto> {
        return accessibilityReportManager.validate(validationId)
    }

    fun upgrade(id: String): List<AccessibilityReportDto> {
        val toBeUpgraded = byId(id)
        if (toBeUpgraded.isEmpty()) return emptyList()
        val reportDto = toBeUpgraded.first()
        val notificationFromReport = getNotificationFromReport(reportDto)
        val createdNotification = accessibilityNotificationManager.create(notificationFromReport)
        if (createdNotification.isEmpty()) {
            throw IllegalStateException()
        }
        reportDto.issueId = createdNotification.first().id
        return update(reportDto)
    }

    fun delete(id: String): List<AccessibilityReportDto> = accessibilityReportManager.delete(id)
    fun count(): Long = accessibilityReportManager.count()
    fun count(realm: String): Long = accessibilityReportManager.count(realm)
    fun countUpgraded(realm: String): Long = accessibilityReportManager.countUpgraded(realm)
    fun countByTrailId(id: String): Long = accessibilityReportManager.countByTrailId(id)
    fun countUnapgraded(realm: String): Long = accessibilityReportManager.countUnapgraded(realm)

    private fun getNotificationFromReport(reportDto: AccessibilityReportDto): AccessibilityNotificationDto {
        val now = Date()
        return AccessibilityNotificationDto(null, reportDto.description, reportDto.trailId, now, now, true, "", reportDto.coordinates, null)
    }

}