package org.sc.manager

import org.sc.common.rest.AccessibilityReportDto
import org.sc.data.mapper.AccessibilityReportMapper
import org.sc.data.model.RecordDetails
import org.sc.data.repository.AccessibilityReportDao
import org.sc.util.StringIdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityReportManager @Autowired constructor(
        private val accessibilityMapper: AccessibilityReportMapper,
        private val accessibilityReportDAO: AccessibilityReportDao,
        private val stringIdGenerator: StringIdGenerator
) {

    fun getById(id: String): List<AccessibilityReportDto> =
            accessibilityReportDAO.getById(id).map { accessibilityMapper.map(it) }

    fun getActivationIdById(id: String) : List<String> =
            accessibilityReportDAO.getActivationIdById(id)

    fun getUnapgradedByRealm(realm: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportDAO.getUnapgradedByRealm(realm, skip, limit).map { accessibilityMapper.map(it) }

    fun getUpgradedByRealm(realm: String, skip: Int, limit: Int): List<AccessibilityReportDto> =
            accessibilityReportDAO.getUpgradedByRealm(realm, skip, limit).map { accessibilityMapper.map(it) }

    fun getByTrailId(trailId: String, skip: Int, limit: Int): List<AccessibilityReportDto> {
        val solved = accessibilityReportDAO.getByTrailId(trailId, skip, limit)
        return solved.map { accessibilityMapper.map(it) }
    }

    fun create(accessibilityNotificationCreation: AccessibilityReportDto, instance: String, realm: String): List<AccessibilityReportDto> {
        val mapped = accessibilityMapper.map(accessibilityNotificationCreation)
        mapped.reportDate = Date()
        mapped.recordDetails = RecordDetails(
                Date(),
                accessibilityNotificationCreation.email,
                instance, realm)

        return accessibilityReportDAO.upsert(mapped, stringIdGenerator.generate())
                .map { accessibilityMapper.map(it) }
    }

    fun validate(validationId: String): List<AccessibilityReportDto> {
        return accessibilityReportDAO.validate(validationId).map { accessibilityMapper.map(it) }
    }

    fun update(accReport: AccessibilityReportDto): List<AccessibilityReportDto> {
        return accessibilityReportDAO.update(accessibilityMapper.map(accReport))
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