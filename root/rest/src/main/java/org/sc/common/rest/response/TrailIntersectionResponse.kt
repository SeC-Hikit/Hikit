package org.sc.common.rest.response

import org.sc.common.rest.PoiDto
import org.sc.common.rest.Status
import org.sc.common.rest.TrailIntersectionDto

data class TrailIntersectionResponse (val status: Status,
                                      val messages: Set<String>,
                                      val content: List<TrailIntersectionDto>,
                                      override val currentPage: Long,
                                      override val totalPages: Long,
                                      override val size: Long,
                                      override val totalCount: Long) :
    RESTResponse(currentPage, totalPages, size, totalCount)