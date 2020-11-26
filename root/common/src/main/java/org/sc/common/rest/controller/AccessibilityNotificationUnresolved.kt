package org.sc.common.rest.controller

import java.util.*

data class AccessibilityNotificationUnresolved (val _id: String, val description: String, val code: String,
                                                val reportDate: Date, val isMinor: Boolean)