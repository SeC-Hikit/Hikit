package org.sc.common.rest.response

import org.sc.common.rest.AccessibilityUnresolvedDto
import org.sc.common.rest.Status

data class AccessibilityUnresolvedResponse (
    val status: Status,
    val messages: Set<String>,
    val accessibilityNotifications : List<AccessibilityUnresolvedDto>)
    : RESTResponse()