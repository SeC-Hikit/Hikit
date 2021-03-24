package org.sc.common.rest.response

import org.sc.common.rest.MaintenanceDto
import org.sc.common.rest.Status

data class MaintenanceResponse(
    val status: Status,
    val messages: Set<String>,
    val content: List<MaintenanceDto>,
    override val currentPage: Long,
    override val totalPages: Long,
    override val size: Long,
    override val totalCount: Long
) :
    RESTResponse(currentPage, totalPages, size, totalCount)