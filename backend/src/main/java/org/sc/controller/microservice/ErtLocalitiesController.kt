package org.sc.controller.microservice

import io.swagger.v3.oas.annotations.Operation
import org.hikit.common.ControllerConstants
import org.hikit.common.response.ControllerPagination
import org.openapitools.model.LocalityResponse
import org.sc.adapter.microservice.ErtLocalityMicroserviceAdapter
import org.sc.common.rest.CoordinatesDto
import org.sc.data.validator.GeneralValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnExpression("\${microservice.ert.localities.enabled:false}")
@RequestMapping(ErtLocalitiesController.PREFIX)
class ErtLocalitiesController @Autowired constructor(
    private val ertLocalityMicroserviceAdapter: ErtLocalityMicroserviceAdapter,
    private val generalValidator: GeneralValidator,
    private val controllerPagination: ControllerPagination
) {
    companion object {
        const val PREFIX = "/ert/localities"
    }

    @Operation(summary = "Retrieve localities by distance from a point")
    @GetMapping(produces = ["application/json"])
    operator fun get(
        @RequestParam(required = false, defaultValue = ControllerConstants.MIN_DOCS_ON_READ) skip: Int,
        @RequestParam(required = false, defaultValue = ControllerConstants.MAX_DOCS_ON_READ) limit: Int,
        @RequestParam(required = true) latitude: Double,
        @RequestParam(required = true) longitude: Double,
        @RequestParam(required = true) distance: Double
    ): LocalityResponse? {
        val validate = generalValidator.validate(CoordinatesDto(latitude, longitude))
        controllerPagination.checkSkipLim(skip, limit)
        if (validate.isNotEmpty()) throw IllegalArgumentException()
        return ertLocalityMicroserviceAdapter.get(latitude, longitude, distance, skip, limit)!!.body
    }
}