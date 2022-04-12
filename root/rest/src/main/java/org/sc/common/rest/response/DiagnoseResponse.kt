package org.sc.common.rest.response

import java.util.*

data class DiagnoseResponse (
        val service : String,
        val success: Boolean,
        val time: Date
)