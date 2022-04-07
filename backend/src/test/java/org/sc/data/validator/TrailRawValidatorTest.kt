package org.sc.data.validator

import io.mockk.every
import io.mockk.mockkClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.sc.common.rest.TrailRawDto
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.TrailRawManager

internal class TrailRawValidatorTest {

    private val rawManager: TrailRawManager =
            mockkClass(TrailRawManager::class)

    private val coordinatesValidator: CoordinatesValidator =
            mockkClass(CoordinatesValidator::class)

    private val trailCoordinatesValidator: TrailCoordinatesValidator =
            mockkClass(TrailCoordinatesValidator::class)

    private val authRealmValidator: AuthRealmValidator =
            mockkClass(AuthRealmValidator::class)

    @Test
    fun `validating a request with different realm shall return error`() {
        val trailRawDtoById =
                mockkClass(TrailRawDto::class)

        val myRealm = "myRealm"

        every { trailRawDtoById.fileDetails.realm } returns myRealm
        every { rawManager.getById(any()) } returns listOf(trailRawDtoById)
        every { authRealmValidator.isAdminSameRealmAsResource(myRealm) } returns false

        val sut = TrailRawValidator(coordinatesValidator, trailCoordinatesValidator, authRealmValidator, rawManager)

        val expectedErrorList = sut.validateDeleteRequest("myId")

        assertThat(expectedErrorList.first()).isEqualTo("Realm mismatch")
    }

}