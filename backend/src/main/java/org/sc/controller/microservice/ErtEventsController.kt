package org.sc.controller.microservice

import io.swagger.v3.oas.annotations.Operation
import org.hikit.common.ControllerConstants
import org.openapitools.model.EventResponse
import org.sc.adapter.microservice.ErtEventMicroserviceAdapter
import org.sc.data.validator.GeneralValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.*

@RestController
@ConditionalOnExpression("\${microservice.ert.events.enabled:false}")
@RequestMapping(ErtEventsController.PREFIX)
class ErtEventsController @Autowired constructor(
    private val ertEventMicroserviceAdapter: ErtEventMicroserviceAdapter,
    private val generalValidator: GeneralValidator,
) {
    companion object {
        const val PREFIX = "/ert/events"
    }

    @Operation(summary = "Retrieve events by istat code")
    @GetMapping("/{istat}", produces = ["application/json"])
    operator fun get(
        @PathVariable(required = true) istat: String,
        @RequestParam(required = false, defaultValue = ControllerConstants.MIN_DOCS_ON_READ) skip: Int,
        @RequestParam(required = false, defaultValue = ControllerConstants.MAX_DOCS_ON_READ) limit: Int,
    ): EventResponse? {
        val validationErrors = generalValidator.validateIstat(istat)
        if(validationErrors.isNotEmpty()) {
            composeErrorResponse(validationErrors)
        }
        return ertEventMicroserviceAdapter.getByIstat1(istat, skip, limit)!!.body
    }

    private fun composeErrorResponse(validationErrors: Set<String>) {
        EventResponse()
            .status(EventResponse.StatusEnum.ERROR)
            .messages(validationErrors)
            .totalCount(0)
            .totalPages(0)
            .content(listOf())
            .currentPage(0)
    }
}