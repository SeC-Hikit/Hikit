package org.sc.common.rest.response

import org.sc.common.rest.PlaceDto
import org.sc.common.rest.Status

data class PlaceResponse(
    val status: Status,
    val messages: Set<String>,
    val content: List<PlaceDto>,
    override val currentPage: Long,
    override val totalPages: Long,
    override val size: Long,
    override val totalCount: Long
) :
    RESTResponse(currentPage, totalPages, size, totalCount)

