package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test
import org.sc.common.rest.geo.RectangleDto

class RectangleValidatorTest {

    private val coordsValidatorMock: CoordinatesValidator =
            mockkClass(CoordinatesValidator::class)

    @Test
    fun `validation should fail on rectangle view with diagonal length greater than 50 km`() {
        every { coordsValidatorMock.validate(any())} returns emptySet()

        val rectangleValidator = RectangleValidator(coordsValidatorMock)
        val requestMock = mockkClass(RectangleDto::class)

        every { requestMock.bottomLeft.latitude} returns 44.0
        every { requestMock.bottomLeft.longitude} returns 10.8

        every { requestMock.topRight.latitude} returns 48.2
        every { requestMock.topRight.longitude} returns 11.2

        val validateResult = rectangleValidator.validate(requestMock)
        assertTrue(validateResult.contains(RectangleValidator.diagonalLengthErrorLower))
        }

        @Test
    fun `validation should fail from La Spezia to Venezia rectangle`() {
        every { coordsValidatorMock.validate(any())} returns emptySet()

        val rectangleValidator = RectangleValidator(coordsValidatorMock)
        val requestMock = mockkClass(RectangleDto::class)

        every { requestMock.bottomLeft.latitude} returns 44.065485
        every { requestMock.bottomLeft.longitude} returns 9.816693

        every { requestMock.topRight.latitude} returns 45.469377
        every { requestMock.topRight.longitude} returns 12.317288

        val validateResult = rectangleValidator.validate(requestMock)
        assertTrue(validateResult.contains(RectangleValidator.diagonalLengthErrorLower))
    }

    @Test
    fun `validation shall pass on rectangle view with diagonal length shorter than 50 km (Bologna to Sasso Marconi)`() {
        every { coordsValidatorMock.validate(any())} returns emptySet()

        val rectangleValidator = RectangleValidator(coordsValidatorMock)
        val requestMock = mockkClass(RectangleDto::class)

        every { requestMock.bottomLeft.latitude} returns 44.400480
        every { requestMock.bottomLeft.longitude} returns 11.250650

        every { requestMock.topRight.latitude} returns 44.494888
        every { requestMock.topRight.longitude} returns 11.342616

        val validateResult = rectangleValidator.validate(requestMock)
        assertTrue(validateResult.isEmpty())
    }
}
