package org.sc.manager

import org.sc.common.rest.MaintenanceCreationDto
import org.sc.common.rest.MaintenanceDto
import org.sc.data.dto.MaintenanceMapper
import org.sc.data.repository.MaintenanceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MaintenanceManager @Autowired constructor(
    private val maintenanceDao: MaintenanceDAO,
    private val maintenanceMapper: MaintenanceMapper) {

    fun getFuture(page: Int, count: Int): List<MaintenanceDto> =
        maintenanceDao.getFuture(page, count).map { maintenanceMapper.map(it) }


    fun getPast(page: Int, count: Int): List<MaintenanceDto> =
        maintenanceDao.getPast(page, count).map { maintenanceMapper.map(it) }

    fun getPastMaintenanceForTrailCode(trailCode: String, page: Int, count: Int): List<MaintenanceDto> =
        maintenanceDao.getPastForTrailCode(trailCode, page, count).map { maintenanceMapper.map(it) }

    fun upsert(request: MaintenanceCreationDto): List<MaintenanceDto> =
        maintenanceDao.upsert( maintenanceMapper.map(request) ).map { maintenanceMapper.map(it) }


    fun delete(id: String): List<MaintenanceDto> =
      maintenanceDao.delete(id).map { maintenanceMapper.map(it) }

    fun countMaintenance(): Long = maintenanceDao.countMaintenance()

}