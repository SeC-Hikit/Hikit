package org.sc.common.rest.response

import org.sc.common.rest.MediaDto
import org.sc.common.rest.Status

data class MediaCreationResponse(
    val status: Status,
    val messages: Set<String>,
    val content: List<MediaDto>
) : RESTResponse()