package org.sc.common.rest.response

import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.Status

data class AccessibilityResponse (val status: Status,
                                  val messages: Set<String>,
                                  val accessibilityNotifications : List<AccessibilityNotificationDto>) :
    RESTResponse(status, messages)