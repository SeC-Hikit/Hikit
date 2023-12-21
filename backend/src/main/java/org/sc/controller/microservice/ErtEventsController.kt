package org.sc.controller.microservice

import io.swagger.v3.oas.annotations.Operation
import org.hikit.common.response.ControllerPagination
import org.openapitools.model.EventResponse
import org.sc.adapter.microservice.ErtEventMicroserviceAdapter
import org.sc.data.validator.GeneralValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnExpression("\${microservice.ert.events.enabled:false}")
@RequestMapping(ErtEventsController.PREFIX)
class ErtEventsController @Autowired constructor(
    private val ertEventMicroserviceAdapter: ErtEventMicroserviceAdapter,
    private val generalValidator: GeneralValidator,
    private val controllerPagination: ControllerPagination
) {
    companion object {
        const val PREFIX = "/ert/events"
    }

    @Operation(summary = "Retrieve events by istat code")
    @GetMapping("/{istat}", produces = ["application/json"])
    operator fun get(
        @PathVariable(required = true) istat: String
    ): EventResponse? {
        return ertEventMicroserviceAdapter.getByIstat1(istat)!!.body
    }
}