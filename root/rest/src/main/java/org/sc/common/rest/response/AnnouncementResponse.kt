package org.sc.common.rest.response

import org.sc.common.rest.AnnouncementDto
import org.sc.common.rest.Status

data class AnnouncementResponse(
        val status: Status,
        val messages: Set<String>,
        val content: List<AnnouncementDto>,
        override val currentPage: Long,
        override val totalPages: Long,
        override val size: Long,
        override val totalCount: Long
) :
        RESTResponse(currentPage, totalPages, size, totalCount)