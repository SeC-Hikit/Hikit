package org.sc.common.rest.controller

import java.util.*

data class AccessibilityNotificationCreation (val code: String, val description: String,
                                              val reportDate: Date, val isMinor: Boolean)