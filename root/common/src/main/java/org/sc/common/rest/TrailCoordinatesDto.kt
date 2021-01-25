package org.sc.common.rest

data class TrailCoordinatesDto(
    override val latitude: Double, override val longitude: Double,
    override val altitude: Double, val distanceFromTrailStart: Double
) : CoordinatesDto(latitude, longitude, altitude)