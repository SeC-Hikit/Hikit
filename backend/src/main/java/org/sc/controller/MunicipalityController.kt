package org.sc.controller

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.Status
import org.sc.common.rest.response.MunicipalityResponse
import org.sc.service.MunicipalityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(MunicipalityController.PREFIX)
class MunicipalityController @Autowired constructor(
    private val municipalityService: MunicipalityService,
) {
    companion object {
        const val PREFIX = "/municipality"
    }

    @GetMapping
    @Operation(summary = "Get all municipalities")
    fun get(): MunicipalityResponse {
        val result = municipalityService.getDistinctMunicipality()
        return MunicipalityResponse(
            Status.OK, emptySet(), result, 1L,
            1L, result.size.toLong(), result.size.toLong()
        )
    }
}