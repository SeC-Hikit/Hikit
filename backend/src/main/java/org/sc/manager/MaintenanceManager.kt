package org.sc.manager

import org.sc.common.rest.MaintenanceDto
import org.sc.common.rest.RecordDetailsDto
import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.MaintenanceMapper
import org.sc.data.repository.MaintenanceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class MaintenanceManager @Autowired constructor(
    private val maintenanceDao: MaintenanceDAO,
    private val maintenanceMapper: MaintenanceMapper,
    private val authFacade: AuthFacade
) {

    fun getFuture(page: Int, count: Int): List<MaintenanceDto> =
        maintenanceDao.getFuture(page, count).map { maintenanceMapper.map(it) }


    fun getById(id: String): List<MaintenanceDto> {
        return maintenanceDao.getById(id).map {maintenanceMapper.map(it)};
    }

    fun getPast(page: Int, count: Int): List<MaintenanceDto> =
        maintenanceDao.getPast(page, count).map { maintenanceMapper.map(it) }

    fun getPastMaintenanceForTrailId(trailCode: String, page: Int, count: Int): List<MaintenanceDto> =
        maintenanceDao.getPastForTrailCode(trailCode, page, count).map { maintenanceMapper.map(it) }

    fun create(request: MaintenanceDto): List<MaintenanceDto> {
        val authHelper = authFacade.authHelper
        request.recordDetails = RecordDetailsDto(
            Date(), authHelper.username, authHelper.instance, authHelper.realm
        )
        return maintenanceDao
            .upsert(maintenanceMapper.map(request))
            .map { maintenanceMapper.map(it) }
    }

    fun delete(id: String): List<MaintenanceDto> =
      maintenanceDao.delete(id).map { maintenanceMapper.map(it) }

    fun countMaintenance(): Long = maintenanceDao.countMaintenance()

    fun countPastMaintenance(): Long = maintenanceDao.countPastMaintenance()
    fun countFutureMaintenance(): Long = maintenanceDao.countFutureMaintenance()


}