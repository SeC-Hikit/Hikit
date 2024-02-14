package org.sc.data.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class IstatValidatorTest {

    @Test
    fun `validation shall provide no errors on correct istat `() {
        assertThat(IstatValidator().validate("40034")).isEmpty()
    }

    @Test
    fun `validation shall provide error on wrong istat `() {
        assertThat(IstatValidator().validate("4003a")).isNotEmpty
    }

    @Test
    fun `validation shall provide error on empty istat `() {
        assertThat(IstatValidator().validate("")).isNotEmpty
    }

}