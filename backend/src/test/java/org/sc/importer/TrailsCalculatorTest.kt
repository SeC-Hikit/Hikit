package org.sc.importer

import io.mockk.every
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Test
import org.sc.common.rest.controller.TrailCoordinates

class TrailsCalculatorTest {

    @Test
    fun `calculate time distance between four points on same elevation`(){
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)
        val mockPoint4 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint1.latitude } returns 44.49397
        every { mockPoint1.longitude } returns 11.31086

        every { mockPoint2.altitude } returns 1.0
        every { mockPoint2.latitude } returns 44.49354
        every { mockPoint2.longitude } returns 11.30792

        every { mockPoint3.altitude } returns 0.0
        every { mockPoint3.latitude } returns 44.49319
        every { mockPoint3.longitude } returns 11.30577

        every { mockPoint4.altitude } returns 1.0
        every { mockPoint4.latitude } returns 44.49298
        every { mockPoint4.longitude } returns 11.30439

        TrailsCalculator()
                .calculateEta(listOf(mockPoint1, mockPoint2, mockPoint3, mockPoint4))
    }

    @Test
    fun `calculate rise, given two points with no rise should return 0`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 0.0

        assertEquals(0.0,
                TrailsCalculator()
                        .calculateTotRise(listOf(mockPoint1, mockPoint2)), 0.0
        )
    }

    @Test
    fun `calculate rise, given two points with fall should return 0`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns -1.0

        assertEquals(0.0,
                TrailsCalculator()
                        .calculateTotRise(listOf(mockPoint1, mockPoint2)), 0.0
        )
    }

    @Test
    fun `calculate rise, given two points with rise should calculate rise`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 10.0

        assertEquals(10.0,
                TrailsCalculator()
                        .calculateTotRise(listOf(mockPoint1, mockPoint2)), 0.0
        )
    }

    @Test
    fun `calculate rise, given three points with rise should calculate rise`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 10.0
        every { mockPoint3.altitude } returns 22.0

        assertEquals(22.0,
                TrailsCalculator()
                        .calculateTotRise(
                                listOf(mockPoint1, mockPoint2, mockPoint3)), 0.0
        )
    }

    @Test
    fun `calculate rise, given four points with rise and fall should calculate rise only`() {

        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)
        val mockPoint4 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 10.0
        every { mockPoint3.altitude } returns 5.0
        every { mockPoint4.altitude } returns 10.0

        assertEquals(15.0,
                TrailsCalculator()
                        .calculateTotRise(
                                listOf(mockPoint1, mockPoint2, mockPoint3, mockPoint4)), 0.0
        )
    }

    @Test
    fun `calculate rise, given six points with rise and fall should calculate rise only`() {

        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)
        val mockPoint4 = mockkClass(TrailCoordinates::class)
        val mockPoint5 = mockkClass(TrailCoordinates::class)
        val mockPoint6 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 10.0
        every { mockPoint3.altitude } returns 5.0
        every { mockPoint4.altitude } returns 10.0
        every { mockPoint5.altitude } returns 115.0
        every { mockPoint6.altitude } returns -120.0

        assertEquals(120.0,
                TrailsCalculator()
                        .calculateTotRise(
                                listOf(mockPoint1, mockPoint2, mockPoint3, mockPoint4, mockPoint5, mockPoint6)), 0.0
        )
    }


    @Test
    fun `calculate fall, given two points with no fall should return 0`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 0.0

        assertEquals(0.0,
                TrailsCalculator()
                        .calculateTotFall(listOf(mockPoint1, mockPoint2)), 0.0
        )
    }

    @Test
    fun `calculate fall, given two points with rise should return 0`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns 1.0

        assertEquals(0.0,
                TrailsCalculator()
                        .calculateTotFall(listOf(mockPoint1, mockPoint2)), 0.0
        )
    }

    @Test
    fun `calculate fall, given two points with fall should calculate fall`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns -10.0

        assertEquals(10.0,
                TrailsCalculator()
                        .calculateTotFall(listOf(mockPoint1, mockPoint2)), 0.0
        )
    }

    @Test
    fun `calculate fall, given three points with fall should calculate tot fall`() {
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns -10.0
        every { mockPoint3.altitude } returns -22.0

        assertEquals(22.0,
                TrailsCalculator()
                        .calculateTotFall(
                                listOf(mockPoint1, mockPoint2, mockPoint3)), 0.0
        )
    }

    @Test
    fun `calculate fall, given four points with fall and rise should calculate fall only`() {

        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)
        val mockPoint4 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint2.altitude } returns -10.0
        every { mockPoint3.altitude } returns -5.0
        every { mockPoint4.altitude } returns -10.0

        assertEquals(15.0,
                TrailsCalculator()
                        .calculateTotFall(
                                listOf(mockPoint1, mockPoint2, mockPoint3, mockPoint4)), 0.0
        )
    }

    @Test
    fun `calculate distance between two points but selecting the first one`(){
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint1.latitude } returns 44.501026
        every { mockPoint1.longitude } returns 11.321588


        every { mockPoint2.altitude } returns 0.0
        every { mockPoint2.latitude } returns 44.497017
        every { mockPoint2.longitude } returns 11.318903

        assertEquals(0,
                TrailsCalculator().calculateLengthFromTo(
                        listOf(mockPoint1, mockPoint2), mockPoint1))
    }


    @Test
    fun `calculate distance between two points`(){
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint1.latitude } returns 44.501026
        every { mockPoint1.longitude } returns 11.321588


        every { mockPoint2.altitude } returns 0.0
        every { mockPoint2.latitude } returns 44.497017
        every { mockPoint2.longitude } returns 11.318903

        assertEquals(495,
                TrailsCalculator().calculateLengthFromTo(
                                listOf(mockPoint1, mockPoint2), mockPoint2))
    }

    @Test
    fun `calculate distance between three points`(){
        val mockPoint1 = mockkClass(TrailCoordinates::class)
        val mockPoint2 = mockkClass(TrailCoordinates::class)
        val mockPoint3 = mockkClass(TrailCoordinates::class)

        every { mockPoint1.altitude } returns 0.0
        every { mockPoint1.latitude } returns 44.501026
        every { mockPoint1.longitude } returns 11.321588


        every { mockPoint2.altitude } returns 0.0
        every { mockPoint2.latitude } returns 44.497017
        every { mockPoint2.longitude } returns 11.318903

        every { mockPoint3.altitude } returns 0.0
        every { mockPoint3.latitude } returns 44.497895
        every { mockPoint3.longitude } returns 11.313611

        assertEquals(926,
                TrailsCalculator().calculateLengthFromTo(
                        listOf(mockPoint1, mockPoint2, mockPoint3), mockPoint3))
    }

}