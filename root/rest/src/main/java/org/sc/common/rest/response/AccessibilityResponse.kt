package org.sc.common.rest.response

import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.Status

data class AccessibilityResponse(
    val status: Status,
    val messages: Set<String>,
    val content: List<AccessibilityNotificationDto>,
    override val currentPage: Long,
    override val totalPages: Long,
    override val size: Long,
    override val totalCount: Long
) :
    RESTResponse(currentPage, totalPages, size, totalCount)