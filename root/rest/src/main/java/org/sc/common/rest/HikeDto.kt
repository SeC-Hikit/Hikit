package org.sc.common.rest

import org.sc.data.model.TrailStatus
import java.util.*

data class HikeDto(
    val id: String,
    var name: String,
    var coverMedia: LinkedMediaDto,
    val description: String,
    val shortDescription: String,
    val statsTrailMetadata: StatsTrailMetadataDto,
    val lastUpdate: Date,
    var status: TrailStatus,
    val fileDetails: FileDetailsDto,
    val staticTrailDetails: StaticTrailDetailsDto,
    val municipalities: List<MunicipalityDetailsDto>,
)