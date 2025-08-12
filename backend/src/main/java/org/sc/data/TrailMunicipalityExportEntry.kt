package org.sc.data

import org.sc.common.rest.PlaceRefDto
import java.util.Date

data class TrailMunicipalityExportEntry(
    val trailCode : String,
    val otherMunicipalities: List<String>,
    val distanceInMunicipality: Double,
    val startingPlace: PlaceRefDto,
    val endingPlace: PlaceRefDto,
    val localities: List<PlaceRefDto>,
    val totalTrailDistance: Double,
    val lastUpdate: Date,
)
