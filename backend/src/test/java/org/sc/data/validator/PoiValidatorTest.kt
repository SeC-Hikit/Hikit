package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test
import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.KeyValueDto
import org.sc.common.rest.PoiDto
import org.sc.data.model.PoiMacroType
import org.sc.data.validator.auth.AuthRealmValidator
import java.lang.String.format
import org.sc.data.validator.poi.PoiValidator
import org.sc.data.validator.trail.TrailExistenceValidator
import org.sc.manager.PoiManager
import org.sc.manager.TrailManager
import java.util.*

class PoiValidatorTest {

    companion object {
        const val ANY = "ANY"
    }

    private val trailCoordsValidatorMock: CoordinatesValidator =
            mockkClass(CoordinatesValidator::class)

    private val trailExistenceValidator: TrailExistenceValidator =
            mockkClass(TrailExistenceValidator::class)


    private val keyValValidatorMock: KeyValValidator =
            mockkClass(KeyValValidator::class)

    private val poiManagerMock: PoiManager =
            mockkClass(PoiManager::class)

    private val authRealmValidator: AuthRealmValidator =
            mockkClass(AuthRealmValidator::class)


    @Test
    fun `validation shall pass when all data correct`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(), listOf(), emptyList(), null
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        assertTrue(
                PoiValidator(
                        keyValValidatorMock,
                        trailCoordsValidatorMock,
                        poiManagerMock,
                        trailExistenceValidator,
                        authRealmValidator
                ).validate(poiDto).isEmpty()
        )
    }

    @Test
    fun `validation should fail on missing name`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, "", ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(), listOf(), emptyList(), null
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        assertTrue(
                PoiValidator(
                        keyValValidatorMock,
                        trailCoordsValidatorMock,
                        poiManagerMock,
                        trailExistenceValidator,
                        authRealmValidator
                ).validate(poiDto).isNotEmpty()
        )
    }

    @Test
    fun `validation should fail creation on created after now`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(System.currentTimeMillis() + 10000), Date(), listOf(), emptyList(), null
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate =
                PoiValidator(keyValValidatorMock, trailCoordsValidatorMock, poiManagerMock, trailExistenceValidator, authRealmValidator).validate(
                        poiDto
                )
        assertTrue(validate.isNotEmpty())
        validate.contains(String.format(PoiValidator.dateInFutureError, org.sc.data.model.Poi.CREATED_ON))
    }

    @Test
    fun `validation should fail creation on update after now`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(System.currentTimeMillis() + 10000), listOf(), emptyList(),
                null
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate =
                PoiValidator(keyValValidatorMock, trailCoordsValidatorMock, poiManagerMock, trailExistenceValidator, authRealmValidator).validate(
                        poiDto
                )
        assertTrue(validate.isNotEmpty())
        validate.contains(String.format(PoiValidator.dateInFutureError, org.sc.data.model.Poi.LAST_UPDATE_ON))
    }

    @Test
    fun `validation should fail on non acceptable keyval`() {
        val anyTrailRequestPosMock = mockkClass(CoordinatesDto::class)
        val keyValValue = KeyValueDto("", "abc")
        val poiDto = PoiDto(
                null, ANY, ANY, listOf(), PoiMacroType.BELVEDERE, listOf(), listOf(), listOf(),
                anyTrailRequestPosMock, Date(), Date(), listOf(), listOf(keyValValue), null
        )
        val expectedValue = format(
                ValidatorUtils.emptyFieldError,
                "key"
        )
        every { keyValValidatorMock.validate(keyValValue) } returns setOf(
                expectedValue
        )
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        val validate = PoiValidator(keyValValidatorMock, trailCoordsValidatorMock, poiManagerMock, trailExistenceValidator, authRealmValidator)
                .validate(poiDto)
        assertTrue(validate.isNotEmpty())
        validate.contains(expectedValue)
    }


}