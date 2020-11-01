package org.sc.data

data class TrailPreparationModel constructor(val name: String,
                                             val description: String,
                                             val firstPos: CoordinatesWithAltitude,
                                             val lastPos: CoordinatesWithAltitude,
                                             val coordinates: List<CoordinatesWithAltitude>)