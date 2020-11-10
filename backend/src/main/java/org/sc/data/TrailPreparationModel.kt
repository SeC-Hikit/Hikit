package org.sc.data

import org.sc.common.rest.controller.CoordinatesWithAltitude

data class TrailPreparationModel constructor(val name: String,
                                             val description: String,
                                             val firstPos: CoordinatesWithAltitude,
                                             val lastPos: CoordinatesWithAltitude,
                                             val coordinates: List<CoordinatesWithAltitude>)