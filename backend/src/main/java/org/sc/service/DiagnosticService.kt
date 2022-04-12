package org.sc.service

import org.sc.adapter.AltitudeServiceAdapter
import org.sc.common.rest.response.DiagnoseResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@Service
class DiagnosticService @Autowired constructor(
        private val altitudeServiceAdapter: AltitudeServiceAdapter) {

    companion object {
        const val altitudeServiceName = "Altitude Service"
    }

    fun testAltitudeService() : CompletionStage<DiagnoseResponse> {
        val hasCallMadeCorrectly = altitudeServiceAdapter.getElevationsByLongLat(0.0, 0.0)
        return CompletableFuture.completedFuture(DiagnoseResponse(
                altitudeServiceName, hasCallMadeCorrectly.isNotEmpty(), Date()))
    }


}