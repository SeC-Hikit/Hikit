package org.sc.manager

import org.sc.common.rest.MaintenanceDto
import org.sc.data.repository.MaintenanceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MaintenanceManager @Autowired constructor(val maintenanceDao: MaintenanceDAO) {

    fun getFuture(page: Int, count: Int): MutableList<MaintenanceDto> {
        TODO("Not yet implemented")
    }

    fun getPast(page: Int, count: Int): MutableList<MaintenanceDto> {
        TODO("Not yet implemented")
    }

    fun upsert(request: MaintenanceDto): MutableList<MaintenanceDto> {
        TODO("Not yet implemented")
    }

    fun delete(id: String): List<MaintenanceDto> {
        TODO("Not yet implemented")
    }

}