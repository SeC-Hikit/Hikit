package org.sc.service.response


data class AltitudeApiResponse constructor(val results: List<AltitudeDataPoint> = listOf())
data class AltitudeDataPoint constructor(val latitude: Double = 0.0, val longitude: Double = 0.0, val elevation: Double = 0.0)
