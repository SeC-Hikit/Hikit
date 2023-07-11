package org.sc.service

import org.sc.common.rest.MunicipalityDetailsDto
import org.sc.data.mapper.MunicipalityMapper
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MunicipalityService @Autowired constructor(
    private val trailManager: TrailManager,
    private val municipalityMapper: MunicipalityMapper
) {
    fun getDistinctMunicipality(): List<MunicipalityDetailsDto> {
        return trailManager.getMunicipality().map {  municipalityMapper.map(it) }
    }
}