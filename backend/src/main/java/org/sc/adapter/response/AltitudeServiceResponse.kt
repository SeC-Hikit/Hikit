package org.sc.adapter.response


data class AltitudeApiResponse constructor(val results: List<AltitudeDataPoint> = listOf())
data class AltitudeDataPoint constructor(val latitude: Double = 0.0, val longitude: Double = 0.0, val elevation: Double = 0.0)

data class AltitudeServiceRequest constructor (val locations: List<AltitudeApiRequestPoint> = mutableListOf())
data class AltitudeApiRequestPoint constructor(val latitude: Double, val longitude: Double)