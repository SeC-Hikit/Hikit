package org.sc.data.geo

import io.mockk.mockkClass
import org.junit.Test
import org.sc.data.mapper.PlaceRefMapper
import org.sc.data.mapper.TrailCoordinatesMapper
import org.sc.data.model.CoordinatesWithAltitude
import org.sc.data.model.PlaceRef
import org.sc.data.model.TrailCoordinates
import kotlin.test.assertEquals

internal class TrailPlacesAlignerTest {

    @Test
    fun `given a simple straight trail, with wide distant points, the place refs must be in order`() {

        val trailCoordinatesMapperMock = mockkClass(TrailCoordinatesMapper::class)
        val placeRefMapperMock = mockkClass(PlaceRefMapper::class)
        val sut = TrailPlacesAligner(trailCoordinatesMapperMock, placeRefMapperMock)


        val start = TrailCoordinates(44.46861, 11.09924, 30.0, 0)
        val middle1 = TrailCoordinates(44.47069, 11.11144, 20.0, 3000)
        val middle2 = TrailCoordinates(44.47499, 11.13308, 70.0, 4500)
        val end = TrailCoordinates(44.47469, 11.20395, 90.0, 6500)

        val startPlaceRef = PlaceRef("Start: Monteveglio",
                CoordinatesWithAltitude(44.46861, 11.09924, 30.0),
                "any_id",
                listOf()
        )

        val middlePlaceRef = PlaceRef("middlePlace: Oliveto",
                CoordinatesWithAltitude(44.47340, 11.12309, 40.0),
                "any_id",
                listOf()
        )

        val endPlaceRef = PlaceRef("End: Near Rivabella",
                CoordinatesWithAltitude(44.47469, 11.20395, 90.0),
                "any_other_id",
                listOf()
        )

        val coordinates = listOf(start, middle1, middle2, end)
        val locations = listOf(endPlaceRef, startPlaceRef, middlePlaceRef)

        val sortLocationsByTrailCoordinates = sut.sortLocationsByTrailCoordinates(coordinates, locations)

        assertEquals(startPlaceRef, sortLocationsByTrailCoordinates[0])
        assertEquals(middlePlaceRef, sortLocationsByTrailCoordinates[1])
        assertEquals(endPlaceRef, sortLocationsByTrailCoordinates[2])
    }

    @Test
    fun `given a curvy trail with distant points, the place refs must be in order`() {

        val trailCoordinatesMapperMock = mockkClass(TrailCoordinatesMapper::class)
        val placeRefMapperMock = mockkClass(PlaceRefMapper::class)
        val sut = TrailPlacesAligner(trailCoordinatesMapperMock, placeRefMapperMock)

        val coordinates = listOf(
                TrailCoordinates(44.13889749875123414, 11.13477380042232312, 600.0, 0),
                TrailCoordinates(44.13884769932957397, 11.13463459955685941, 600.0, 20),
                TrailCoordinates(44.13882379864526229, 11.13438410025043979, 600.0, 40),
                TrailCoordinates(44.13878499946299172, 11.1340274993959234, 600.0, 60),
                TrailCoordinates(44.13898119920953178, 11.1354816004038959, 600.0, 80),
                TrailCoordinates(44.13897699930537755, 11.13521009987549526, 600.0, 100),
                TrailCoordinates(44.13897949868925252, 11.13508100012611024, 600.0, 120),
                TrailCoordinates(44.13827729928424759, 11.13284750010623014, 600.0, 140),
                TrailCoordinates(44.13830349884892712, 11.1326868997145052, 600.0, 160),
                TrailCoordinates(44.13829939870672803, 11.13260640015630187, 600.0, 180),
                TrailCoordinates(44.13826719904383822, 11.13248130053447049, 600.0, 200),
                TrailCoordinates(44.13864669925092699, 11.13358650047437415, 600.0, 220),
                TrailCoordinates(44.13847689924833162, 11.13335480006953837, 600.0, 240),
                TrailCoordinates(44.13844609889846993, 11.13314379970479173, 600.0, 260),
                TrailCoordinates(44.13831509922956542, 11.13298619967792291, 600.0, 280),
                TrailCoordinates(44.13768809914090951, 11.13185359964116117, 600.0, 300),
                TrailCoordinates(44.13767419885695631, 11.13155600051061356, 600.0, 320),
                TrailCoordinates(44.1376317986249731, 11.13133490032608108, 600.0, 340),
                TrailCoordinates(44.13754039877708379, 11.13112179969814441, 600.0, 360))

        val startPlaceRef = PlaceRef("Start: Monte Baducco",
                CoordinatesWithAltitude(44.1388974, 11.13477, 600.0),
                "any_id",
                listOf()
        )

        val middlePlaceRef1 = PlaceRef("middlePlace: Woods",
                CoordinatesWithAltitude(44.138328, 11.133494, 620.0),
                "any_idPlace",
                listOf()
        )

        val middlePlaceRef2 = PlaceRef("middlePlace2: Woods",
                CoordinatesWithAltitude(44.138210,11.133053, 620.0),
                "any_idPlace",
                listOf()
        )

        val middlePlaceRef3 = PlaceRef("middlePlace3: Woods",
                CoordinatesWithAltitude(44.137801, 11.132006, 630.0),
                "any_other_id",
                listOf()
        )

        val endPlaceRef = PlaceRef("End: middle of the woods",
                CoordinatesWithAltitude(44.13754039877708379, 11.13112179969814441, 600.0),
                "final_id",
                listOf()
        )

        // Random order
        val locations = listOf(endPlaceRef, startPlaceRef, middlePlaceRef3, middlePlaceRef2, middlePlaceRef1)

        val sortLocationsByTrailCoordinates = sut.sortLocationsByTrailCoordinates(coordinates, locations)

        assertEquals(startPlaceRef, sortLocationsByTrailCoordinates[0])
        assertEquals(middlePlaceRef1, sortLocationsByTrailCoordinates[1])
        assertEquals(middlePlaceRef2, sortLocationsByTrailCoordinates[2])
        assertEquals(middlePlaceRef3, sortLocationsByTrailCoordinates[3])
        assertEquals(endPlaceRef, sortLocationsByTrailCoordinates[4])
    }

}