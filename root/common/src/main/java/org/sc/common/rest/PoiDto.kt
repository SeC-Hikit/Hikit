package org.sc.common.rest

import java.util.*

data class PoiDto (val id: String,
                   val name: String,
                   val description: String,
                   val tags: List<String>,
                   val macroType: PoiMacroType,
                   val microType: List<String>,
                   val mediaIds: List<String>,
                   val trailIds: List<String>,
                   val trailCoordinates: TrailCoordinates,
                   val createdOn: Date,
                   val lastUpdatedOn: Date)