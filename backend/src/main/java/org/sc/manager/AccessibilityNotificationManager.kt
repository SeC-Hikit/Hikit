package org.sc.manager

import org.sc.common.rest.AccessibilityNotificationCreationDto
import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.AccessibilityNotificationResolutionDto
import org.sc.data.mapper.AccessibilityNotificationMapper
import org.sc.data.repository.AccessibilityNotificationDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AccessibilityNotificationManager @Autowired constructor(
    private val accessibilityDAO: AccessibilityNotificationDAO,
    private val accessibilityMapper: AccessibilityNotificationMapper,
) {

    fun getSolved(skip: Int, limit: Int): List<AccessibilityNotificationDto> {
        val solved = accessibilityDAO.getSolved(skip, limit)
        return solved.map { accessibilityMapper.map(it) }
    }

    fun getResolvedById(code: String, skip: Int, limit: Int): List<AccessibilityNotificationDto> {
        val solved = accessibilityDAO.getResolvedByTrailId(code, skip, limit)
        return solved.map { accessibilityMapper.map(it) }
    }

    fun getUnresolved(skip: Int, limit: Int): List<AccessibilityNotificationDto> {
        val unresolved = accessibilityDAO.getUnresolved(skip, limit)
        return unresolved.map { accessibilityMapper.map(it) }
    }

    fun getUnresolvedById(code: String, skip: Int, limit: Int): List<AccessibilityNotificationDto> {
        val unresolved = accessibilityDAO.getUnresolvedByTrailId(code, skip, limit)
        return unresolved.map { accessibilityMapper.map(it) }
    }

    fun resolve(accessibilityRes: AccessibilityNotificationResolutionDto) =
        accessibilityDAO.resolve(accessibilityRes).map { accessibilityMapper.map(it) }


    fun delete(objectId: String): List<AccessibilityNotificationDto> =
        accessibilityDAO.delete(objectId).map { accessibilityMapper.map(it) }


    fun upsert(accessibilityNotificationCreation: AccessibilityNotificationCreationDto): List<AccessibilityNotificationDto> =
        accessibilityDAO.insert(accessibilityNotificationCreation)
            .map { accessibilityMapper.map(it) }

    fun count(): Long = accessibilityDAO.countAccessibility()
    fun countSolved(): Long = accessibilityDAO.countSolved()
    fun countNotSolved(): Long = accessibilityDAO.countNotSolved()
    fun countSolvedForTrailId(trailId: String): Long = accessibilityDAO.countSolvedForTrailId(trailId)
    fun countNotSolvedForTrailId(trailId: String): Long = accessibilityDAO.countNotSolvedForTrailId(trailId)

}