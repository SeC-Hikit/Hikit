package org.sc.data.validator

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test
import org.sc.common.rest.MaintenanceDto
import org.sc.data.validator.MaintenanceValidator.Companion.dateInPast
import java.util.*

class MaintenanceValidatorTest {

    @Test
    fun `validation shall pass when all data correct`() {

        val maintenanceCreationValidator = MaintenanceValidator()

        val requestMock = mockkClass(MaintenanceDto::class)

        every { requestMock.meetingPlace } returns "La via"
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() + 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.isEmpty())
    }

    @Test
    fun `validation errors contain missing meeting place when not given`() {

        val maintenanceCreationValidator = MaintenanceValidator()

        val requestMock = mockkClass(MaintenanceDto::class)

        every { requestMock.meetingPlace } returns ""
        every { requestMock.code } returns "100BO"
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() + 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Meeting Place'"))
        assertTrue(validateResult.size == 1)
    }

    @Test
    fun `validation errors contain missing meeting place and code when not given`() {

        val maintenanceCreationValidator = MaintenanceValidator()

        val requestMock = mockkClass(MaintenanceDto::class)

        every { requestMock.meetingPlace } returns ""
        every { requestMock.code } returns ""
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() + 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains("Empty field 'Meeting Place'"))
        assertTrue(validateResult.contains("Empty field 'Code'"))
        assertTrue(validateResult.size == 2)
    }

    @Test
    fun `validation errors contain date in past if a past date given`() {

        val maintenanceCreationValidator = MaintenanceValidator()

        val requestMock = mockkClass(MaintenanceDto::class)

        every { requestMock.meetingPlace } returns "a place"
        every { requestMock.code } returns "100bo"
        every { requestMock.description } returns "A description"
        every { requestMock.date } returns Date(System.currentTimeMillis() - 1000 * 60 * 24)
        every { requestMock.contact } returns "Anybody"

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains(dateInPast))
        assertTrue(validateResult.size == 1)
    }

    @Test
    fun `validation errors contain 'date in past' if a past date given and missing contact`() {

        val maintenanceCreationValidator = MaintenanceValidator()

        val requestMock = mockkClass(MaintenanceDto::class)

        every { requestMock.meetingPlace } returns "a place"
        every { requestMock.code } returns "100bo"
        every { requestMock.description } returns ""
        every { requestMock.date } returns Date(System.currentTimeMillis() - 1000 * 60 * 24)
        every { requestMock.contact } returns ""

        val validateResult = maintenanceCreationValidator.validate(requestMock)
        assertTrue(validateResult.contains(dateInPast))
        assertTrue(validateResult.contains("Empty field 'Contact'"))
        assertTrue(validateResult.size == 2)
    }


}