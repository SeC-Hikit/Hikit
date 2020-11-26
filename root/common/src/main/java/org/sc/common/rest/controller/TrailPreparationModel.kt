package org.sc.common.rest.controller

data class TrailPreparationModel constructor(val name: String,
                                             val description: String,
                                             val startPos: Position,
                                             val finalPos: Position,
                                             val coordinates: List<TrailCoordinates>)