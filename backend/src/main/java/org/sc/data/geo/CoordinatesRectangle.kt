package org.sc.data.geo

import org.sc.data.model.Coordinates2D

data class CoordinatesRectangle(val topLeft : Coordinates2D, val topRight: Coordinates2D,
                                val bottomLeft: Coordinates2D, val bottomRight: Coordinates2D)
