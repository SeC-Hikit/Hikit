package org.sc.common.rest.response

import org.sc.common.rest.Status
import org.sc.common.rest.TrailMappingDto
import org.sc.common.rest.TrailPreviewDto

data class TrailMappingResponse (val status: Status,
                                 val messages: Set<String>,
                                 val content: List<TrailMappingDto>,
                                 override val currentPage: Long,
                                 override val totalPages: Long,
                                 override val size: Long,
                                 override val totalCount: Long) :
    RESTResponse(currentPage, totalPages, size, totalCount)