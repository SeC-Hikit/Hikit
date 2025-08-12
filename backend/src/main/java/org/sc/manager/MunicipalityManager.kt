package org.sc.manager

import org.sc.data.model.MunicipalityDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MunicipalityManager @Autowired constructor(
        private val trailManager: TrailManager,
) {

    fun getAvailable(): List<MunicipalityDetails> =
            trailManager.getMunicipalities()
}