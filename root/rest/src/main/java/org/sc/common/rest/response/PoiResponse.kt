package org.sc.common.rest.response

import org.sc.common.rest.PoiDto
import org.sc.common.rest.Status

data class PoiResponse(
    val status: Status,
    val messages: Set<String>,
    val content: List<PoiDto>,
    override val currentPage: Long,
    override val totalPages: Long,
    override val size: Long,
    override val totalCount: Long
) :
    RESTResponse(currentPage, totalPages, size, totalCount)
