package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test
import org.sc.common.rest.MaintenanceDto
import org.sc.data.validator.MaintenanceValidator.Companion.dateInPast
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.data.validator.trail.TrailExistenceValidator
import org.sc.manager.MaintenanceManager
import java.util.*

internal class MaintenanceValidatorTest {

    @Test
    fun `validation shall pass when all data correct`() {

        val maintenanceManagerMock = mockkClass(MaintenanceManager::class)
        val realmValidatorMock = mockkClass(AuthRealmValidator::class)
        val trailExistenceValidator = mockkClass(TrailExistenceValidator::class)


        val maintenanceValidator = MaintenanceValidator(
                maintenanceManagerMock,
                trailExistenceValidator,
                realmValidatorMock,
        )

        val requestMock = mockkClass(MaintenanceDto::class)

        val trailId = "100BO"
        every { requestMock.meetingPlace } returns "La via"
        every { requestMock.trailId } returns trailId
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() + 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"
        every { trailExistenceValidator.validateExistenceAndRealm(trailId) } returns emptySet()

        val validateResult = maintenanceValidator.validate(requestMock)
        assertTrue(validateResult.isEmpty())
    }

    @Test
    fun `validation errors contain missing meeting place when not given`() {

        val maintenanceManagerMock = mockkClass(MaintenanceManager::class)
        val realmValidatorMock = mockkClass(AuthRealmValidator::class)
        val trailExistenceValidator = mockkClass(TrailExistenceValidator::class)


        val maintenanceCreationValidator = MaintenanceValidator(maintenanceManagerMock,trailExistenceValidator, realmValidatorMock)

        val requestMock = mockkClass(MaintenanceDto::class)

        val trailId = "100BO"
        every { requestMock.meetingPlace } returns ""
        every { requestMock.trailId } returns trailId
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() + 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"
        every { trailExistenceValidator.validateExistenceAndRealm(trailId) } returns emptySet()

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Meeting Place'"))
        assertTrue(validateResult.size == 1)
    }

    @Test
    fun `validation errors contain missing meeting place, and trailId when not given`() {

        val maintenanceManagerMock = mockkClass(MaintenanceManager::class)
        val realmValidatorMock = mockkClass(AuthRealmValidator::class)
        val trailExistenceValidator = mockkClass(TrailExistenceValidator::class)

        val maintenanceCreationValidator = MaintenanceValidator(maintenanceManagerMock, trailExistenceValidator, realmValidatorMock)

        val requestMock = mockkClass(MaintenanceDto::class)

        val trailId = ""
        every { requestMock.meetingPlace } returns ""
        every { requestMock.trailId } returns trailId
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() + 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"
        every { trailExistenceValidator.validateExistenceAndRealm(trailId) } returns setOf("Error")

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Meeting Place'"))
        assertTrue(validateResult.contains("Empty field 'Code'"))
        assertTrue(validateResult.contains("Error"))
        assertTrue(validateResult.size == 3)
    }

    @Test
    fun `validation errors contain date in past if a past date given`() {

        val maintenanceManagerMock = mockkClass(MaintenanceManager::class)
        val realmValidatorMock = mockkClass(AuthRealmValidator::class)
        val trailExistenceValidator = mockkClass(TrailExistenceValidator::class)

        val maintenanceCreationValidator = MaintenanceValidator(maintenanceManagerMock, trailExistenceValidator, realmValidatorMock)

        val requestMock = mockkClass(MaintenanceDto::class)


        val trailId = "100bo"
        every { requestMock.meetingPlace } returns "a place"
        every { requestMock.trailId } returns trailId
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() - 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"
        every { trailExistenceValidator.validateExistenceAndRealm(trailId) } returns emptySet()

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains(dateInPast))
        assertTrue(validateResult.size == 1)
    }

    @Test
    fun `validation errors contain 'date in past' if a past date given and missing contact`() {

        val maintenanceManagerMock = mockkClass(MaintenanceManager::class)
        val realmValidatorMock = mockkClass(AuthRealmValidator::class)
        val trailExistenceValidator = mockkClass(TrailExistenceValidator::class)

        val maintenanceCreationValidator = MaintenanceValidator(maintenanceManagerMock, trailExistenceValidator, realmValidatorMock)

        val requestMock = mockkClass(MaintenanceDto::class)


        val trailId = "100bo"
        every { requestMock.meetingPlace } returns "a place"
        every { requestMock.trailId } returns trailId
        every { requestMock.description } returns ""
        every { requestMock.date } returns Date(System.currentTimeMillis() - 1000 * 60 * 24)
        every { requestMock.contact } returns ""
        every { trailExistenceValidator.validateExistenceAndRealm(trailId) } returns emptySet()

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains(dateInPast))
        assertTrue(validateResult.contains("Empty field 'Contact'"))
        assertTrue(validateResult.size == 2)
    }


}