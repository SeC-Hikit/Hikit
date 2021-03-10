package org.sc.common.rest.response

import org.sc.common.rest.LinkedMediaResultDto
import org.sc.common.rest.Status

data class LinkedMediaResponse(val status: Status,
                               val messages: Set<String>,
                               val content: List<LinkedMediaResultDto>)