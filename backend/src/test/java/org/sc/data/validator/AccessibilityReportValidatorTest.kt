package org.sc.data.validator

import io.mockk.every
import io.mockk.mockkClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.sc.common.rest.AccessibilityReportDto
import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.TrailCoordinatesDto
import org.sc.common.rest.TrailDto
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.AccessibilityReportManager
import org.sc.manager.TrailManager
import org.sc.processor.TrailSimplifierLevel

internal class AccessibilityReportValidatorTest {

    private val expectedTrailId = "any"
    private val anyEmail = "any@any.com"
    private val anyDescription = "Trail is damaged"
    
    private val authRealmValidator = mockkClass(AuthRealmValidator::class)
    private val accessibilityNotificationManager = mockkClass(AccessibilityReportManager::class)
    private val coordinatesValidator = mockkClass(CoordinatesValidator::class)
    private val trailManager = mockkClass(TrailManager::class)

    @Test
    fun `validation shall provide error on too distant position `() {

        val sut =
                AccessibilityReportValidator(
                        authRealmValidator, accessibilityNotificationManager,
                        coordinatesValidator, trailManager)

        val request = mockkClass(AccessibilityReportDto::class)
        val expectedCoordinates = mockkClass(CoordinatesDto::class)

        val expectedTrail = mockkClass(TrailDto::class);
        // Sut
        val userPosition = getTooDistantUserCoordsMock()

        val firstCoord = getFirstCoord()
        val secondCoord = getSecondCoord()
        val thirdCoord = getThirdCoord()

        // Molino - Bolsenda
        val expectedCoordsInTrail =
                listOf(firstCoord, secondCoord, thirdCoord)

        mockSuccessCollaboration(expectedTrail, expectedCoordsInTrail,
                request, userPosition, expectedCoordinates)


        val errors = sut.validate(request)

        assertThat(errors).contains(AccessibilityReportValidator.placeTooFarErrorMessage)
    }

    @Test
    fun `validation shall pass`() {
        val sut =
                AccessibilityReportValidator(
                        authRealmValidator, accessibilityNotificationManager,
                        coordinatesValidator, trailManager)

        val request = mockkClass(AccessibilityReportDto::class)
        val expectedCoordinates = mockkClass(CoordinatesDto::class)

        val expectedTrail = mockkClass(TrailDto::class);
        val userPosition = getCloseUserCoordsMock()

        val firstCoord = getFirstCoord()
        val secondCoord = getSecondCoord()
        val thirdCoord = getThirdCoord()

        // Molino - Bolsenda
        val expectedCoordsInTrail =
                listOf(firstCoord, secondCoord, thirdCoord)

        mockSuccessCollaboration(expectedTrail, expectedCoordsInTrail, request, userPosition, expectedCoordinates)

        val errors = sut.validate(request)

        assertThat(errors).isEmpty()
    }

    private fun mockSuccessCollaboration(expectedTrail: TrailDto,
                                         expectedCoordsInTrail: List<TrailCoordinatesDto>,
                                         request: AccessibilityReportDto,
                                         userPosition: CoordinatesDto,
                                         expectedCoordinates: CoordinatesDto) {
        every { expectedTrail.coordinates } returns expectedCoordsInTrail
        every { request.trailId } returns expectedTrailId
        every { request.coordinates } returns userPosition
        every { request.email } returns anyEmail
        every { request.description } returns anyDescription

        every { trailManager.doesTrailExist(expectedTrailId) } returns true
        every { coordinatesValidator.validate(userPosition) } returns emptySet()
        every { coordinatesValidator.validate(expectedCoordinates) } returns emptySet()

        every {
            trailManager.getById(expectedTrailId,
                    TrailSimplifierLevel.HIGH)
        } returns listOf(expectedTrail)

        every {
            trailManager.getById(expectedTrailId,
                    TrailSimplifierLevel.HIGH)
        } returns listOf(expectedTrail)
    }


    private fun getFirstCoord(): TrailCoordinatesDto {
        val firstCoordPoint = mockkClass(TrailCoordinatesDto::class)
        every { firstCoordPoint.latitude } returns 44.453388
        every { firstCoordPoint.longitude } returns 11.257521
        every { firstCoordPoint.altitude } returns 0.0
        every { firstCoordPoint.distanceFromTrailStart } returns 0
        return firstCoordPoint
    }

    private fun getSecondCoord(): TrailCoordinatesDto {
        val secondCoord = mockkClass(TrailCoordinatesDto::class)
        every { secondCoord.latitude } returns 44.450894
        every { secondCoord.longitude } returns 11.249746
        every { secondCoord.altitude } returns 0.0
        every { secondCoord.distanceFromTrailStart } returns 100
        return secondCoord
    }

    private fun getThirdCoord(): TrailCoordinatesDto {
        val thirdCoord = mockkClass(TrailCoordinatesDto::class)
        every { thirdCoord.latitude } returns 44.448294
        every { thirdCoord.longitude } returns 11.241577
        every { thirdCoord.altitude } returns 0.0
        every { thirdCoord.distanceFromTrailStart } returns 200
        return thirdCoord
    }

    private fun getTooDistantUserCoordsMock(): CoordinatesDto {
        val userPosition = mockkClass(CoordinatesDto::class)
        every { userPosition.latitude } returns 44.440834
        every { userPosition.longitude } returns 11.251723
        every { userPosition.altitude } returns 0.0
        return userPosition
    }

    private fun getCloseUserCoordsMock(): CoordinatesDto {
        val userPosition = mockkClass(CoordinatesDto::class)
        every { userPosition.latitude } returns 44.451508
        every { userPosition.longitude } returns 11.252806
        every { userPosition.altitude } returns 0.0
        return userPosition
    }

}