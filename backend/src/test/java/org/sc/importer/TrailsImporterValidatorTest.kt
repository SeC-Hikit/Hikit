package org.sc.importer

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.sc.common.rest.Position
import org.sc.common.rest.TrailClassification
import org.sc.common.rest.TrailCoordinates
import org.sc.data.TrailImport
import org.sc.data.validator.PositionCreationValidator
import org.sc.data.validator.TrailCoordinatesCreationValidator
import org.sc.data.validator.TrailImportValidator
import org.sc.data.validator.TrailImportValidator.Companion.dateInFutureError
import org.sc.data.validator.TrailImportValidator.Companion.posToTrailCoordError
import java.util.*

class TrailsImporterValidatorTest {

    private val trailCoordsValidatorMock: TrailCoordinatesCreationValidator =
            mockkClass(TrailCoordinatesCreationValidator::class)

    private val positionCreationValidatorMock: PositionCreationValidator =
            mockkClass(PositionCreationValidator::class)

    @Test
    fun `validation shall pass when all data correct`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, positionCreationValidatorMock)

        val startPosMock = mockkClass(Position::class)
        val finalPosMock = mockkClass(Position::class)
        val anyTrailRequestPosMock = mockkClass(TrailCoordinates::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinates::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinates::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        every { positionCreationValidatorMock.validate(startPosMock) } returns emptySet()
        every { positionCreationValidatorMock.validate(finalPosMock) } returns emptySet()

        every { startPosMock.coordinates } returns startTrailCoordsPosMock
        every { finalPosMock.coordinates } returns finalTrailCoordsPosMock

        val requestMock = mockkClass(TrailImport::class)

        every { requestMock.name } returns "La via"
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.startPos } returns startPosMock
        every { requestMock.finalPos } returns finalPosMock
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.isEmpty())
    }


    @Test
    fun `validation should fail on missing name`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, positionCreationValidatorMock)

        val startPosMock = mockkClass(Position::class)
        val finalPosMock = mockkClass(Position::class)
        val anyTrailRequestPosMock = mockkClass(TrailCoordinates::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinates::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinates::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        every { positionCreationValidatorMock.validate(startPosMock) } returns emptySet()
        every { positionCreationValidatorMock.validate(finalPosMock) } returns emptySet()

        every { startPosMock.coordinates } returns startTrailCoordsPosMock
        every { finalPosMock.coordinates } returns finalTrailCoordsPosMock


        val requestMock = mockkClass(TrailImport::class)

        every { requestMock.name } returns ""
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.startPos } returns startPosMock
        every { requestMock.finalPos } returns finalPosMock
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

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, positionCreationValidatorMock)

        val startPosMock = mockkClass(Position::class)
        val finalPosMock = mockkClass(Position::class)
        val anyTrailRequestPosMock = mockkClass(TrailCoordinates::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinates::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinates::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        every { positionCreationValidatorMock.validate(startPosMock) } returns emptySet()
        every { positionCreationValidatorMock.validate(finalPosMock) } returns emptySet()

        every { startPosMock.coordinates } returns startTrailCoordsPosMock
        every { finalPosMock.coordinates } returns finalTrailCoordsPosMock

        val requestMock = mockkClass(TrailImport::class)

        every { requestMock.name } returns ""
        every { requestMock.code } returns ""
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.startPos } returns startPosMock
        every { requestMock.finalPos } returns finalPosMock
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

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, positionCreationValidatorMock)

        val startPosMock = mockkClass(Position::class)
        val finalPosMock = mockkClass(Position::class)
        val anyTrailRequestPosMock = mockkClass(TrailCoordinates::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinates::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinates::class)

        val errorWithTrail = "Error with trail"
        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf(errorWithTrail)
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        every { positionCreationValidatorMock.validate(startPosMock) } returns emptySet()
        every { positionCreationValidatorMock.validate(finalPosMock) } returns emptySet()

        every { startPosMock.coordinates } returns startTrailCoordsPosMock
        every { finalPosMock.coordinates } returns finalTrailCoordsPosMock

        val requestMock = mockkClass(TrailImport::class)

        every { requestMock.name } returns ""
        every { requestMock.code } returns ""
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.startPos } returns startPosMock
        every { requestMock.finalPos } returns finalPosMock
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
    fun `validation fails when first pos is not first trail coord`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, positionCreationValidatorMock)

        val startPosMock = mockkClass(Position::class)
        val finalPosMock = mockkClass(Position::class)
        val anyTrailRequestPosMock = mockkClass(TrailCoordinates::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinates::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinates::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        every { positionCreationValidatorMock.validate(startPosMock) } returns emptySet()
        every { positionCreationValidatorMock.validate(finalPosMock) } returns emptySet()

        every { startPosMock.coordinates } returns startTrailCoordsPosMock
        every { finalPosMock.coordinates } returns finalTrailCoordsPosMock

        val requestMock = mockkClass(TrailImport::class)

        every { requestMock.name } returns "Trail name"
        every { requestMock.code } returns "001BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.startPos } returns startPosMock
        every { requestMock.finalPos } returns finalPosMock
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date()
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(anyTrailRequestPosMock, startTrailCoordsPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains(posToTrailCoordError))
        assertEquals(1, validateResult.size)
    }

    @Test
    fun `validation fails afterhour`() {

        val trailsImporterValidator = TrailImportValidator(trailCoordsValidatorMock, positionCreationValidatorMock)

        val startPosMock = mockkClass(Position::class)
        val finalPosMock = mockkClass(Position::class)
        val anyTrailRequestPosMock = mockkClass(TrailCoordinates::class)
        val startTrailCoordsPosMock = mockkClass(TrailCoordinates::class)
        val finalTrailCoordsPosMock = mockkClass(TrailCoordinates::class)

        every { trailCoordsValidatorMock.validate(anyTrailRequestPosMock) } returns setOf()
        every { trailCoordsValidatorMock.validate(startTrailCoordsPosMock) } returns emptySet()
        every { trailCoordsValidatorMock.validate(finalTrailCoordsPosMock) } returns emptySet()

        every { positionCreationValidatorMock.validate(startPosMock) } returns emptySet()
        every { positionCreationValidatorMock.validate(finalPosMock) } returns emptySet()

        every { startPosMock.coordinates } returns startTrailCoordsPosMock
        every { finalPosMock.coordinates } returns finalTrailCoordsPosMock

        val requestMock = mockkClass(TrailImport::class)

        every { requestMock.name } returns "Trail name"
        every { requestMock.code } returns "001BO"
        every { requestMock.description } returns "A description"
        every { requestMock.classification } returns TrailClassification.E
        every { requestMock.startPos } returns startPosMock
        every { requestMock.finalPos } returns finalPosMock
        every { requestMock.country } returns "Italy"
        every { requestMock.lastUpdate } returns Date(System.currentTimeMillis() + 10000)
        every { requestMock.locations } returns emptyList()
        every { requestMock.coordinates } returns listOf(startTrailCoordsPosMock, anyTrailRequestPosMock, finalTrailCoordsPosMock)


        val validateResult = trailsImporterValidator.validate(requestMock)
        assertTrue(validateResult.contains(dateInFutureError))
        assertEquals(1, validateResult.size)
    }



}