package org.sc.manager

import org.sc.common.rest.AccessibilityNotificationCreationDto
import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.AccessibilityNotificationResolutionDto
import org.sc.common.rest.AccessibilityUnresolvedDto
import org.sc.data.dto.AccessibilityNotificationMapper
import org.sc.data.dto.AccessibilityNotificationUnrMapper
import org.sc.data.repository.AccessibilityNotificationDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AccessibilityNotificationManager @Autowired constructor(
    private val accessibilityDAO: AccessibilityNotificationDAO,
    private val accessibilityMapper : AccessibilityNotificationMapper,
    private val accessibilityNotificationUnrMapper: AccessibilityNotificationUnrMapper) {

    fun getSolved(page: Int, count: Int) : List<AccessibilityNotificationDto> {
        val solved = accessibilityDAO.getSolved(page, count)
        return solved.map { accessibilityMapper.accessibilityNotificationToAccessibilityNotificationDto(it) }
    }

    fun getResolvedByCode(code: String) : List<AccessibilityNotificationDto> {
        val solved = accessibilityDAO.getResolvedByCode(code)
        return solved.map { accessibilityMapper.accessibilityNotificationToAccessibilityNotificationDto(it) }
    }

    fun getUnresolved(page: Int, count: Int) : List<AccessibilityUnresolvedDto> {
        val unresolved = accessibilityDAO.getUnresolved(page, count)
        return unresolved.map { accessibilityNotificationUnrMapper.accessibilityNotificationToAccessibilityUnresolvedDto(it) }
    }

    fun getUnresolvedByCode(code: String) : List<AccessibilityUnresolvedDto> {
        val unresolved = accessibilityDAO.getUnresolvedByCode(code)
        return unresolved.map { accessibilityNotificationUnrMapper.accessibilityNotificationToAccessibilityUnresolvedDto(it) }
    }

    fun resolve(accessibilityRes: AccessibilityNotificationResolutionDto) =
        listOf(accessibilityDAO.resolve(accessibilityRes)).map { accessibilityMapper.accessibilityNotificationToAccessibilityNotificationDto(it) }


    fun delete(objectId: String) : List<AccessibilityNotificationDto> =
        listOf(accessibilityDAO.delete(objectId)).map { accessibilityMapper.accessibilityNotificationToAccessibilityNotificationDto(it) }


    fun upsert(accessibilityNotificationCreation: AccessibilityNotificationCreationDto): List<AccessibilityUnresolvedDto> =
        listOf(accessibilityDAO.insert(accessibilityNotificationCreation)).map { accessibilityNotificationUnrMapper.accessibilityNotificationToAccessibilityUnresolvedDto(it) }

}