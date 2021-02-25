package org.sc.common.rest.response

import org.sc.common.rest.CountDto
import org.sc.common.rest.Status

data class CountResponse(
    val status: Status,
    val message: Set<String>,
    val content: CountDto
)
