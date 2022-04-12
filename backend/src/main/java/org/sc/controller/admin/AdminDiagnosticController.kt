package org.sc.controller.admin

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.response.DiagnoseResponse
import org.sc.controller.admin.Constants.PREFIX_DIAGNOSE
import org.sc.service.DiagnosticService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture


@RestController
@RequestMapping(PREFIX_DIAGNOSE)
class AdminDiagnosticController @Autowired constructor(private val diagnosticService: DiagnosticService) {

    @Operation(summary = "Get the altitude service status")
    @GetMapping("/altitude")
    fun testAltitude() : CompletableFuture<DiagnoseResponse> = diagnosticService.testAltitudeService().toCompletableFuture()

    @Operation(summary = "Get the service status")
    @GetMapping("/weather")
    fun testWeather() : CompletableFuture<DiagnoseResponse> = throw NotImplementedError()
}