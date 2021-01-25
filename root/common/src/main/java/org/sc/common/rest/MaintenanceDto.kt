package org.sc.common.rest

import java.util.*


data class MaintenanceDto (
    val date: Date,
    val code: String,
    val meetingPlace: String,
    val description: String,
    val contact: String)
