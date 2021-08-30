package org.sc.manager

import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.AccessibilityReportMapper
import org.sc.data.repository.AccessibilityReportDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AccessibilityReportManager @Autowired constructor(
        private val accessibilityMapper: AccessibilityReportMapper,
        private val accessibilityReportDAO: AccessibilityReportDao,
        private val authFacade: AuthFacade,
) {

//    fun byId(id: String): List<AccessibilityNotificationDto> =
//        accessibilityDAO.getById(id).map { accessibilityMapper.map(it) }
//
//    fun getSolved(skip: Int, limit: Int): List<AccessibilityNotificationDto> {
//        val solved = accessibilityDAO.getSolved(skip, limit)
//        return solved.map { accessibilityMapper.map(it) }
//    }
//
//    fun getResolvedByTrailId(trailId: String, skip: Int, limit: Int): List<AccessibilityNotificationDto> {
//        val solved = accessibilityDAO.getResolvedByTrailId(trailId, skip, limit)
//        return solved.map { accessibilityMapper.map(it) }
//    }
//
//    fun getUnresolved(skip: Int, limit: Int): List<AccessibilityNotificationDto> {
//        val unresolved = accessibilityDAO.getUnresolved(skip, limit)
//        return unresolved.map { accessibilityMapper.map(it) }
//    }
//
//    fun getUnresolvedByTrailId(trailId: String, skip: Int, limit: Int): List<AccessibilityNotificationDto> {
//        val unresolved = accessibilityDAO.getUnresolvedByTrailId(trailId, skip, limit)
//        return unresolved.map { accessibilityMapper.map(it) }
//    }
//
//    fun resolve(accessibilityRes: AccessibilityNotificationResolutionDto) =
//        accessibilityDAO.resolve(accessibilityRes).map { accessibilityMapper.map(it) }
//
//
//    fun delete(objectId: String): List<AccessibilityNotificationDto> =
//        accessibilityDAO.delete(objectId).map { accessibilityMapper.map(it) }
//
//
//    fun create(accessibilityNotificationCreation: AccessibilityNotificationDto): List<AccessibilityNotificationDto> {
//        val mapped = accessibilityMapper.map(accessibilityNotificationCreation)
//        val authHelper = authFacade.authHelper
//        mapped.recordDetails = RecordDetails(Date(),
//            authHelper.username,
//            authHelper.instance,
//            authHelper.realm)
//        return accessibilityDAO.insert(mapped)
//            .map { accessibilityMapper.map(it) }
//    }
//
//    fun count(): Long = accessibilityDAO.countAccessibility()
//    fun countSolved(): Long = accessibilityDAO.countSolved()
//    fun countNotSolved(): Long = accessibilityDAO.countNotSolved()
//    fun countSolvedForTrailId(trailId: String): Long = accessibilityDAO.countSolvedForTrailId(trailId)
//    fun countNotSolvedForTrailId(trailId: String): Long = accessibilityDAO.countNotSolvedForTrailId(trailId)

}