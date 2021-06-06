package org.sc.importer

import org.junit.Assert
import org.junit.Test
import org.sc.data.model.TrailCoordinates
import org.sc.processor.TrailSimplifier
import org.sc.processor.TrailSimplifierLevel
import org.sc.util.GpsReadUtils


class TrailSimplifierTest {

    @Test
    fun `simplify a three-coordinate list`() {
        val point1 = TrailCoordinates(44.49441503350789, 11.342670749165524, 10.1, 1)
        val point2 = TrailCoordinates(44.49702, 11.33780, 10.1, 2)
        val point3 = TrailCoordinates(44.50661524800948, 11.307084768272063, 10.1, 3)

        val listMock = listOf(point1, point2, point3)
        val simplifier = TrailSimplifier().simplify(listMock, TrailSimplifierLevel.LOW)
        val len = simplifier.size
        Assert.assertEquals(3, len)
    }

    @Test
    fun `simplify a large coordinate list extracted from file`() {
        val readPoints = GpsReadUtils.readPoints("/points/gps-track.txt");
        val trailCoordinates = readPoints.mapIndexed { index, point ->
            TrailCoordinates(point.y, point.x, 10.0 + index, index)
        }
        Assert.assertEquals(1306, trailCoordinates.size)
        val simplifier = TrailSimplifier().simplify(trailCoordinates, TrailSimplifierLevel.MEDIUM)
        Assert.assertEquals(758, simplifier.size)
    }

    @Test
    fun `do not simplify a small coordinate list extracted from via Emilia`() {
        val point1 = TrailCoordinates(44.49441503350789, 11.342670749165524, 10.1, 1)
        val point2 = TrailCoordinates(44.49702, 11.33780, 10.1, 2)
        val point3 = TrailCoordinates(44.50661524800948, 11.307084768272063, 10.1, 3)
        val point4 = TrailCoordinates(44.516114623497295, 11.278774726084793, 10.1, 4)
        val point5 = TrailCoordinates(44.52202795270847, 11.262027012950172, 10.1, 5)
        val point6 = TrailCoordinates(44.53334299123324, 11.229397220246517, 10.1, 6)
        val point7 = TrailCoordinates(44.5392086884861, 11.21334688154635, 10.1, 7)
        val point8 = TrailCoordinates(44.54638618354076, 11.192012732556414, 10.1, 8)
        val point9 = TrailCoordinates(44.548313008357326, 11.185639803908076, 10.1, 9)
        val point10 = TrailCoordinates(44.55892469362986, 11.157873576156781, 10.1, 10)
        val point11 = TrailCoordinates(44.56860960753179, 11.12869650651551, 10.1, 11)
        val point12 = TrailCoordinates(44.57418897572866, 11.112731998457846, 10.1, 12)
        val point13 = TrailCoordinates(44.58586570145956, 11.08052403263309, 10.1, 13)
        val point14 = TrailCoordinates(44.59888462468006, 11.041728561396981, 10.1, 14)
        val point15 = TrailCoordinates(44.6215538133659, 10.992257898396474, 10.1, 15)
        val point16 = TrailCoordinates(44.63117485010729, 10.961358850562853, 10.1, 16)
        val point17 = TrailCoordinates(44.63502281839996, 10.954191988019865, 10.1, 17)
        val point18 = TrailCoordinates(44.6444889557033, 10.931275194259266, 10.1, 18)
        val point19 = TrailCoordinates(44.695943319981566, 10.634712307950334, 10.1, 19)
        val point20 = TrailCoordinates(44.80427658333105, 10.32764229103951, 10.1, 20)

        val listPoint = listOf(
            point1, point2, point3, point4, point5, point6, point7, point8, point9, point10,
            point11, point12, point13, point14, point15, point16, point17, point18, point19, point20
        )
        val simplifier = TrailSimplifier().simplify(listPoint, TrailSimplifierLevel.LOW)
        Assert.assertEquals(20, simplifier.size)
    }

    @Test
    fun `do not simplify a small coordinate list extracted from a bending trail - 009aBO`() {
        val point1 = TrailCoordinates(44.119134999365968, 11.063886999713153, 10.1, 1)
        val point2 = TrailCoordinates(44.119104698917965, 11.063783800188826, 10.1, 2)
        val point3 = TrailCoordinates(44.119054599162411, 11.063756500442889, 10.1, 3)
        val point4 = TrailCoordinates(44.11892579897998, 11.063736099620412, 10.1, 4)
        val point5 = TrailCoordinates(44.118772699026692, 11.0636644001228, 10.1, 5)
        val point6 = TrailCoordinates(44.118612098929773, 11.063600599490043, 10.1, 6)
        val point7 = TrailCoordinates(44.118519599176665, 11.063624899445767, 10.1, 7)
        val point8 = TrailCoordinates(44.118503799269, 11.063685599520202, 10.1, 8)
        val point9 = TrailCoordinates(44.118563798713595, 11.06382559999876, 10.1, 9)
        val point10 = TrailCoordinates(44.118590398682407, 11.063984799803606, 10.1, 10)
        val point11 = TrailCoordinates(44.118597399467866, 11.064209700187371, 10.1, 11)
        val point12 = TrailCoordinates(44.118568598970242, 11.064382299966192, 10.1, 12)
        val point13 = TrailCoordinates(44.118496798724337, 11.064486999978676, 10.1, 13)
        val point14 = TrailCoordinates(44.118169099177187, 11.064720699369243, 10.1, 14)
        val point15 = TrailCoordinates(44.118098599469647, 11.064753999652863, 10.1, 15)
        val point16 = TrailCoordinates(44.118009799043769, 11.064763599600884, 10.1, 16)
        val point17 = TrailCoordinates(44.117956098813004, 11.06466759976349, 10.1, 17)
        val point18 = TrailCoordinates(44.11790099941021, 11.064596499856702, 10.1, 18)
        val point19 = TrailCoordinates(44.117855999255596, 11.064506299745027, 10.1, 19)
        val point20 = TrailCoordinates(44.117804799452472, 11.064504500014438, 10.1, 20)
        val point21 = TrailCoordinates(44.117775599370795, 11.064532400464358, 10.1, 21)
        val point22 = TrailCoordinates(44.11772259936609, 11.064867999659482, 10.1, 22)
        val point23 = TrailCoordinates(44.117645498949528, 11.065061400101778, 10.1, 23)
        val point24 = TrailCoordinates(44.117543698890088, 11.065164000094882, 10.1, 24)
        val point25 = TrailCoordinates(44.11741649943837, 11.065204500565683, 10.1, 25)
        val point26 = TrailCoordinates(44.11731359936087, 11.065280599935063, 10.1, 26)
        val point27 = TrailCoordinates(44.117106898636166, 11.065489899964959, 10.1, 27)
        val point28 = TrailCoordinates(44.116909798883412, 11.065813899674026, 10.1, 28)
        val point29 = TrailCoordinates(44.116874099258176, 11.066000500042939, 10.1, 29)

        val listPoint = listOf(point1, point2, point3, point4, point5, point6, point7, point8, point9, point10,
                point11, point12, point13, point14, point15, point16, point17, point18, point19, point20,
                point21, point22, point23, point24, point25, point26, point27, point28, point29)
        val simplifier = TrailSimplifier().simplify(listPoint, TrailSimplifierLevel.LOW)
        val len = simplifier.size
        Assert.assertEquals(29, len )

    }
}