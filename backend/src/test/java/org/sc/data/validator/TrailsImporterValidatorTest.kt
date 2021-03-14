package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Test
import org.sc.common.rest.PlaceDto
import org.sc.data.model.TrailClassification
import org.sc.common.rest.TrailCoordinatesDto
import org.sc.common.rest.TrailImportDto
import org.sc.data.validator.TrailImportValidator.Companion.dateInFutureError
import java.util.*

class TrailsImporterValidatorTest {

    private val trailCoordsValidatorMock: TrailCoordinatesValidator =
            mockkClass(TrailCoordinatesValidator::class)

    private val placeValidatorMock: PlaceRefValidator =
            mockkClass(PlaceRefValidator::class)

    @Test
    fun `validation shall pass when all data correct`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, placeValidatorMock)

        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        val requestMock = mockkClass(TrailImportDto::class)

        every { requestMock.name } returns "La via"
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.isEmpty())
    }


    @Test
    fun `validation should fail on missing name`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, placeValidatorMock)

        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        val requestMock = mockkClass(TrailImportDto::class)

        every { requestMock.name } returns ""
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Name'"))
        assertEquals(1, validateResult.size)
    }

    @Test
    fun `validation fails on missing name and code`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, placeValidatorMock)

        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        val requestMock = mockkClass(TrailImportDto::class)

        every { requestMock.name } returns ""
        every { requestMock.code } returns ""
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Name'"))
        assertTrue(validateResult.contains("Empty field 'Code'"))
        assertEquals(2, validateResult.size)
    }

    @Test
    fun `validation fails on wrong trail coord`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, placeValidatorMock)

        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)

        val errorWithTrail = "Error with trail"
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf(errorWithTrail)
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()


        val requestMock = mockkClass(TrailImportDto::class)

        every { requestMock.name } returns ""
        every { requestMock.code } returns ""
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Name'"))
        assertTrue(validateResult.contains("Empty field 'Code'"))
        assertTrue(validateResult.contains(errorWithTrail))
        assertEquals(3, validateResult.size)
    }

    @Test
    fun `validation fails afterhour`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, placeValidatorMock)

        val anyTrailRequestPosMock = mockkClass(TrailCoordinatesDto::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinatesDto::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()


        val requestMock = mockkClass(TrailImportDto::class)

        every { requestMock.name } returns "Trail name"
        every { requestMock.code } returns "001BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date(System.currentTimeMillis() + 10000)
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains(dateInFutureError))
        assertEquals(1, validateResult.size)
    }



}