package org.sc.manager

import org.sc.common.rest.MaintenanceDto
import org.sc.common.rest.RecordDetailsDto
import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.MaintenanceMapper
import org.sc.data.repository.MaintenanceDAO
import org.sc.manager.regeneration.RegenerationActionType
import org.sc.manager.regeneration.RegenerationEntryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class MaintenanceManager @Autowired constructor(
        private val maintenanceDao: MaintenanceDAO,
        private val maintenanceMapper: MaintenanceMapper,
        private val resourceManager: ResourceManager,
        private val authFacade: AuthFacade
) {

    fun getFuture(page: Int, count: Int, realm: String): List<MaintenanceDto> =
            maintenanceDao.getFuture(page, count, getTomorrowDate().toLocalDate(), realm)
                    .map { maintenanceMapper.map(it) }

    fun getById(id: String): List<MaintenanceDto> {
        return maintenanceDao.getById(id).map { maintenanceMapper.map(it) }
    }

    fun getByTrailId(id: String): List<MaintenanceDto> {
        return maintenanceDao.getByTrailId(id).map { maintenanceMapper.map(it) }
    }

    fun getPast(page: Int, count: Int, realm: String): List<MaintenanceDto> =
            maintenanceDao.getPastDate(page, count, getTomorrowDate().toLocalDate(), realm)
                    .map { maintenanceMapper.map(it) }

    fun getPastMaintenanceForTrailId(trailCode: String, page: Int, count: Int): List<MaintenanceDto> =
            maintenanceDao.getPastForTrailCode(trailCode, page, count,
                    getTomorrowDate().toLocalDate())
                    .map { maintenanceMapper.map(it) }

    fun create(request: MaintenanceDto): List<MaintenanceDto> {
        val authHelper = authFacade.authHelper
        request.recordDetails = RecordDetailsDto(
                Date(), authHelper.username, authHelper.instance, authHelper.realm
        )
        val upsert = maintenanceDao
                .upsert(maintenanceMapper.map(request))
        val created = upsert.first()

        // TODO: move this up to Service
        resourceManager.addEntry(created.trailId, RegenerationEntryType.MAINTENANCE,
                created.id, authFacade.authHelper.username,
                RegenerationActionType.CREATE)


        return listOf(maintenanceMapper.map(created))
    }

    fun delete(id: String): List<MaintenanceDto> {
        val delete = maintenanceDao.delete(id)
        val deletedMaintenance = delete.first()

        // TODO: move this up to Service
        resourceManager.addEntry(deletedMaintenance.trailId,
                RegenerationEntryType.MAINTENANCE,
                deletedMaintenance.id,
                authFacade.authHelper.username,
                RegenerationActionType.DELETE)

        return delete.map { maintenanceMapper.map(it) }
    }


    fun countMaintenance(realm: String): Long = maintenanceDao.countMaintenance(realm)
    fun countPastMaintenance(realm: String): Long = maintenanceDao.countPastMaintenance(realm)
    fun countFutureMaintenance(realm: String): Long = maintenanceDao.countFutureMaintenance(realm)

    private fun getTomorrowDate() = LocalDate.now().plusDays(1).atStartOfDay()


}