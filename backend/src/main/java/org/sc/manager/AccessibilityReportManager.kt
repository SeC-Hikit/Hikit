package org.sc.manager

import org.sc.common.rest.AccessibilityReportDto
import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.AccessibilityReportMapper
import org.sc.data.model.RecordDetails
import org.sc.data.repository.AccessibilityReportDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityReportManager @Autowired constructor(
        private val accessibilityMapper: AccessibilityReportMapper,
        private val accessibilityReportDAO: AccessibilityReportDao,
        private val authFacade: AuthFacade,
) {

    fun byId(id: String): List<AccessibilityReportDto> =
            accessibilityReportDAO.getById(id).map { accessibilityMapper.map(it) }

    fun getUnapgradedByRealm(realm: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportDAO.getUnapgradedByRealm(realm, skip, limit).map { accessibilityMapper.map(it) }

    fun getUpgradedByRealm(realm: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportDAO.getUpgradedByRealm(realm, skip, limit).map { accessibilityMapper.map(it) }

    fun getByTrailId(trailId: String, skip: Int, limit: Int): List<AccessibilityReportDto> {
        val solved = accessibilityReportDAO.getByTrailId(trailId, skip, limit)
        return solved.map { accessibilityMapper.map(it) }
    }

    fun save(accessibilityNotificationCreation: AccessibilityReportDto): List<AccessibilityReportDto> {
        val mapped = accessibilityMapper.map(accessibilityNotificationCreation)
        val authHelper = authFacade.authHelper
        mapped.recordDetails = RecordDetails(
                Date(),
                authHelper.username,
                authHelper.instance,
                authHelper.realm)
        return accessibilityReportDAO.upsert(mapped)
                .map { accessibilityMapper.map(it) }
    }

    fun delete(id: String): List<AccessibilityReportDto> = accessibilityReportDAO.delete(id)
            .map { accessibilityMapper.map(it) }

    fun count(): Long = accessibilityReportDAO.count()
    fun count(realm: String): Long = accessibilityReportDAO.countAccessibility(realm)
    fun countUpgraded(realm: String): Long = accessibilityReportDAO.countUpgraded(realm)
    fun countByTrailId(id: String): Long = accessibilityReportDAO.countByTrailId(id)
    fun countUnapgraded(realm: String): Long = accessibilityReportDAO.countUnapgraded(realm)

}