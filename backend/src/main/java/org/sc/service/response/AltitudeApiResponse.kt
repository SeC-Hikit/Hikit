package org.sc.service.response

data class AltitudeApiResponse constructor(val results: List<AltitudeDataPoint>)
data class AltitudeDataPoint constructor(val latitude: Double, val longitude: Double, val elevation: Double)
