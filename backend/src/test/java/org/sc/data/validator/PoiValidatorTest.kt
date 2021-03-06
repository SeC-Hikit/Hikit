package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test
import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.KeyValueDto
import org.sc.common.rest.PoiDto
import org.sc.common.rest.PoiMacroType
import org.sc.data.entity.Poi
import org.sc.data.validator.poi.PoiValidator
import java.lang.String.format
import java.util.*

class PoiValidatorTest {

    companion object{
        const val ANY = "ANY"
    }

    private val trailCoordsValidatorMock: CoordinatesValidator =
        mockkClass(CoordinatesValidator::class)

    private val keyValValidatorMock: KeyValValidator =
        mockkClass(KeyValValidator::class)


    @Test
    fun `validation shall pass when all data correct`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(), listOf(), emptyList()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        assertTrue(PoiValidator(keyValValidatorMock, trailCoordsValidatorMock).validate(poiDto).isEmpty())
    }

    @Test
    fun `validation should fail on missing name`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, "", ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(), listOf(), emptyList()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        assertTrue(PoiValidator(keyValValidatorMock, trailCoordsValidatorMock).validate(poiDto).isNotEmpty())
    }

    @Test
    fun `validation should fail creation on created after now`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(System.currentTimeMillis() + 10000), Date(), listOf(), emptyList()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate = PoiValidator(keyValValidatorMock, trailCoordsValidatorMock).validate(poiDto)
        assertTrue(validate.isNotEmpty())
        validate.contains(String.format(PoiValidator.dateInFutureError, Poi.CREATED_ON))
    }

    @Test
    fun `validation should fail creation on update after now`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(System.currentTimeMillis() + 10000), listOf(), emptyList()
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate = PoiValidator(keyValValidatorMock, trailCoordsValidatorMock).validate(poiDto)
        assertTrue(validate.isNotEmpty())
        validate.contains(String.format(PoiValidator.dateInFutureError, Poi.LAST_UPDATE_ON))
    }

    @Test
    fun `validation should fail on non acceptable keyval`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val keyValValue = KeyValueDto("", "abc")
        val poiDto = PoiDto(
            null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
            anyTrailRequestPosMock, Date(), Date(), listOf(), listOf(keyValValue)
        )
        val expectedValue = format(
            ValidatorUtils.emptyFieldError,
            "key"
        )
        every { keyValValidatorMock.validate(keyValValue) } returns setOf(
            expectedValue
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate = PoiValidator(keyValValidatorMock, trailCoordsValidatorMock).validate(poiDto)
        assertTrue(validate.isNotEmpty())
        validate.contains(expectedValue)
    }


}