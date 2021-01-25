package org.sc.common.rest

import java.util.*

data class AccessibilityNotificationDto (val description: String, val code: String, val reportDate: Date,
                                         val resolutionDate: Date, val isMinor: Boolean, val resolution: String)