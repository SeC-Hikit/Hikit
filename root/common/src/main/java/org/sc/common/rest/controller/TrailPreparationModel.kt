package org.sc.common.rest.controller

data class TrailPreparationModel constructor(val name: String,
                                             val description: String,
                                             val firstPos: TrailCoordinates,
                                             val lastPos: TrailCoordinates,
                                             val coordinates: List<TrailCoordinates>)