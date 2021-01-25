package org.sc.common.rest

import java.util.*

data class AccessibilityUnresolvedDto (val _id: String, val description: String, val code: String,
                                       val reportDate: Date, val isMinor: Boolean)