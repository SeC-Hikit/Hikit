package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.sc.common.rest.PlaceDto
import org.sc.common.rest.PlaceRefDto
import org.sc.data.model.TrailClassification
import org.sc.common.rest.TrailCoordinatesDto
import org.sc.common.rest.TrailImportDto
import org.sc.data.model.PlaceRef
import org.sc.data.validator.TrailImportValidator.Companion.dateInFutureError
import java.util.*

class TrailsImporterValidatorTest {

    private val trailCoordsValidatorMock: TrailCoordinatesValidator =
            mockkClass(TrailCoordinatesValidator::class)

    private val placeValidatorMock: PlaceRefValidator =
            mockkClass(PlaceRefValidator::class)

    private val startPosMock = mockkClass(PlaceRefDto::class)
    private val endPosMock = mockkClass(PlaceRefDto::class)

    @Before
    fun setup () {
        every { placeValidatorMock.validate(startPosMock) } returns emptySet()
        every { placeValidatorMock.validate(endPosMock) } returns emptySet()
    }

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

        every { requestMock.locations } returns listOf(startPosMock, endPosMock)
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

        every { requestMock.name } returns null
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns listOf(startPosMock, endPosMock)
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Name'"))
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

        every { requestMock.name } returns null
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

        every { requestMock.name } returns null
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
    }


}