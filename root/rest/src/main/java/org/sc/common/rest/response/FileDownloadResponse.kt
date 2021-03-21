package org.sc.common.rest.response

import org.sc.common.rest.Status

data class FileDownloadResponse(
    val status: Status,
    val message: Set<String>,
    val path: String,
    override val currentPage: Long,
    override val totalPages: Long,
    override val size: Long,
    override val totalCount: Long
) :
    RESTResponse(currentPage, totalPages, size, totalCount)