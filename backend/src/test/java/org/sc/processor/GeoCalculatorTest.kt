package org.sc.processor

import org.junit.Assert.assertEquals
import org.junit.Test
import org.sc.data.geo.CoordinatesSquare
import org.sc.data.model.Coordinates2D

class GeoCalculatorTest {
    @Test
    fun `calculate box bound given a set of coordinates`() {
        val maxLatitude = 45.620185
        val minLatitude = 43.967517
        val maxLongitude = 12.248740
        val minLongitude = 10.357858

        // Top left
        val mockPoint1 = Coordinates2D(10.577728, 44.534108)
        // Bottom right
        val mockPoint2 = Coordinates2D(11.721052, 44.109676)
        // Bottom left
        val mockPoint3 = Coordinates2D(minLongitude, minLatitude)
        // Top right
        val mockPoint4 = Coordinates2D(maxLongitude, maxLatitude)

        val actual =
            GeoCalculator.getOuterSquareForCoordinates(listOf(mockPoint1, mockPoint2, mockPoint3, mockPoint4))
        println(Coordinates2D(minLatitude, minLongitude))
        println(actual.bottomLeft)
        assertEquals(
            CoordinatesSquare(
                Coordinates2D(minLongitude, maxLatitude),
                Coordinates2D(maxLongitude, maxLatitude),
                Coordinates2D(minLongitude, minLatitude),
                Coordinates2D(maxLongitude, minLatitude)
            ), actual
        )
    }


    @Test
    fun `calculate box bound given a set of coordinates square shaped`() {
        val mockPoint1 = Coordinates2D(0.0, 10.0)
        val mockPoint2 = Coordinates2D(10.0, 10.0)
        val mockPoint3 = Coordinates2D(0.0, 0.0)
        val mockPoint4 = Coordinates2D(10.0, 0.0)

        val actual =
            GeoCalculator.getOuterSquareForCoordinates(listOf(mockPoint1, mockPoint2, mockPoint3, mockPoint4))
        val resultingPolygon = CoordinatesSquare(mockPoint1, mockPoint2, mockPoint3, mockPoint4)
        assertEquals(resultingPolygon, actual)
    }

}