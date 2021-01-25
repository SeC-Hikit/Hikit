package org.sc.common.rest

import java.util.*

data class AccessibilityNotificationCreationDto (val code: String, val description: String,
                                                 val reportDate: Date, val isMinor: Boolean)