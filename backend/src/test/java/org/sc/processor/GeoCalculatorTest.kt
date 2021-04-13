package org.sc.processor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.model.Coordinates2D
import org.sc.data.model.GeoLineString

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
            CoordinatesRectangle(
                    Coordinates2D(minLongitude, minLatitude),Coordinates2D(maxLongitude, maxLatitude)
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
        val resultingPolygon = CoordinatesRectangle(mockPoint3,mockPoint2)
        assertEquals(resultingPolygon, actual)
    }


    @Test
    fun `find intersection between segments in contact`() {
        val subjectSegment = listOf(
            Coordinates2D(0.0, 0.0), Coordinates2D(5.0, 5.0),
            Coordinates2D(10.0, 10.0), Coordinates2D(15.0, 15.0)
        )

        val targetSegment = GeoLineString(listOf(
            Coordinates2D(5.0, 5.0), Coordinates2D(5.0, 3.0),
            Coordinates2D(5.0, 2.0), Coordinates2D(5.0, 0.0)
        ))

        val actual =
            GeoCalculator.getIntersectionPointsBetweenSegments(subjectSegment, targetSegment)

        assertEquals(Coordinates2D(5.0, 5.0), actual.first())
    }

    @Test
    fun `find intersection between lines`() {
        val subjectSegment = listOf(
            Coordinates2D(0.0, 0.0),
            Coordinates2D(10.0, 10.0),
            Coordinates2D(15.0, 15.0)
        )

        val targetSegment = GeoLineString(listOf(
            Coordinates2D(0.0, 10.0),
            Coordinates2D(10.0, 0.0),
            Coordinates2D(10.0, -5.0),
            Coordinates2D(11.0, -6.0)
        ))

        val actual =
            GeoCalculator.getIntersectionPointsBetweenSegments(subjectSegment, targetSegment)

        assertEquals(Coordinates2D(5.0, 5.0), actual.first())
    }

    @Test
    fun `should not find intersection between lines when they are parallel`() {
        val subjectSegment = listOf(
            Coordinates2D(0.0, 0.0),
            Coordinates2D(10.0, 10.0),
            Coordinates2D(15.0, 15.0)
        )

        val targetSegment = GeoLineString(listOf(
            Coordinates2D(  1.0, 0.0),
            Coordinates2D(7.0, 5.0),
            Coordinates2D(12.0, 7.0),
        ))

        val actual =
            GeoCalculator.getIntersectionPointsBetweenSegments(subjectSegment, targetSegment)

        assertTrue(actual.isEmpty())
    }

}