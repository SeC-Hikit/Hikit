package org.sc.common.rest.response

import org.sc.common.rest.MunicipalityDetailsDto
import org.sc.common.rest.Status

data class MunicipalityResponse(
        val status: Status,
        val messages: Set<String>,
        val content: List<MunicipalityDetailsDto>,
        override val currentPage: Long,
        override val totalPages: Long,
        override val size: Long,
        override val totalCount: Long
) :
        RESTResponse(currentPage, totalPages, size, totalCount)