package org.sc.common.rest

data class TrailPreparationModelDto constructor(val name: String,
                                                val description: String,
                                                val startPos: PositionDto,
                                                val finalPos: PositionDto,
                                                val coordinates: List<TrailCoordinatesDto>)