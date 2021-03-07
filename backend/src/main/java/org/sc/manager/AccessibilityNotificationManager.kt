package org.sc.manager

import org.sc.common.rest.AccessibilityNotificationCreationDto
import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.AccessibilityNotificationResolutionDto
import org.sc.common.rest.AccessibilityUnresolvedDto
import org.sc.data.mapper.AccessibilityNotificationMapper
import org.sc.data.repository.AccessibilityNotificationDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AccessibilityNotificationManager @Autowired constructor(
    private val accessibilityDAO: AccessibilityNotificationDAO,
    private val accessibilityMapper: AccessibilityNotificationMapper,
    private val accessibilityNotificationUnrMapper: AccessibilityNotificationMapper
) {

    fun getSolved(page: Int, count: Int): List<AccessibilityNotificationDto> {
        val solved = accessibilityDAO.getSolved(page, count)
        return solved.map { accessibilityMapper.map(it) }
    }

    fun getResolvedByCode(code: String): List<AccessibilityNotificationDto> {
        val solved = accessibilityDAO.getResolvedByCode(code)
        return solved.map { accessibilityMapper.map(it) }
    }

    fun getUnresolved(page: Int, count: Int): List<AccessibilityUnresolvedDto> {
        val unresolved = accessibilityDAO.getUnresolved(page, count)
        return unresolved.map { accessibilityNotificationUnrMapper.map(it) }
    }

    fun getUnresolvedByCode(code: String): List<AccessibilityUnresolvedDto> {
        val unresolved = accessibilityDAO.getUnresolvedByCode(code)
        return unresolved.map { accessibilityNotificationUnrMapper.map(it) }
    }

    fun resolve(accessibilityRes: AccessibilityNotificationResolutionDto) =
        accessibilityDAO.resolve(accessibilityRes).map { accessibilityMapper.map(it) }


    fun delete(objectId: String): List<AccessibilityNotificationDto> =
        accessibilityDAO.delete(objectId).map { accessibilityMapper.map(it) }


    fun upsert(accessibilityNotificationCreation: AccessibilityNotificationCreationDto): List<AccessibilityUnresolvedDto> =
        accessibilityDAO.insert(accessibilityNotificationCreation)
            .map {
                accessibilityNotificationUnrMapper.map(it)
            }

}