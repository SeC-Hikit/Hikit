package org.sc.common.rest.response

import org.sc.common.rest.AccessibilityReportDto
import org.sc.common.rest.Status

data class AccessibilityReportResponse(
        val status: Status,
        val messages: Set<String>,
        val content: List<AccessibilityReportDto>,
        override val currentPage: Long,
        override val totalPages: Long,
        override val size: Long,
        override val totalCount: Long
) :
        RESTResponse(currentPage, totalPages, size, totalCount)