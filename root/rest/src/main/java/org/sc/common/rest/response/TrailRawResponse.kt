package org.sc.common.rest.response

import org.sc.common.rest.Status
import org.sc.common.rest.TrailRawDto

data class TrailRawResponse(
    val status: Status,
    val messages: Set<String>,
    val content: List<TrailRawDto>
) : RESTResponse()