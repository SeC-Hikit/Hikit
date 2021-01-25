package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test
import org.sc.common.rest.PoiDto
import org.sc.common.rest.PoiMacroType
import org.sc.common.rest.TrailCoordinatesDto
import org.sc.data.entity.TrailCoordinates
import org.sc.data.entity.Poi
import java.util.*

class PoiValidatorTest {

    companion object{
        const val ANY = "ANY"
    }

    private val trailCoordsValidatorMock: TrailCoordinatesValidator =
        mockkClass(TrailCoordinatesValidator::class)

    @Test
    fun `validation shall pass when all data correct`() {
        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val poiDto = PoiDto(
            null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
            anyTrailRequestPosMock, Date(), Date(), listOf()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        assertTrue(PoiValidator(trailCoordsValidatorMock).validate(poiDto).isEmpty())
    }

    @Test
    fun `validation should fail on missing name`() {
        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val poiDto = PoiDto(
            null, "", ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
            anyTrailRequestPosMock, Date(), Date(), listOf()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        assertTrue(PoiValidator(trailCoordsValidatorMock).validate(poiDto).isNotEmpty())
    }

    @Test
    fun `validation should fail creation on created after now`() {
        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val poiDto = PoiDto(
            null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
            anyTrailRequestPosMock, Date(System.currentTimeMillis() + 10000), Date(), listOf()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate = PoiValidator(trailCoordsValidatorMock).validate(poiDto)
        assertTrue(validate.isNotEmpty())
        validate.contains(String.format(PoiValidator.dateInFutureError, Poi.CREATED_ON))
    }

    @Test
    fun `validation should fail creation on update after now`() {
        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val poiDto = PoiDto(
            null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
            anyTrailRequestPosMock, Date(), Date(System.currentTimeMillis() + 10000), listOf()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate = PoiValidator(trailCoordsValidatorMock).validate(poiDto)
        assertTrue(validate.isNotEmpty())
        validate.contains(String.format(PoiValidator.dateInFutureError, Poi.LAST_UPDATE_ON))
    }


}