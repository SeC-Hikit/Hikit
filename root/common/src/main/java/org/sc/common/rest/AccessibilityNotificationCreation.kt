package org.sc.common.rest

import java.util.*

data class AccessibilityNotificationCreation (val code: String, val description: String,
                                              val reportDate: Date, val isMinor: Boolean)