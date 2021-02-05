package org.sc.common.rest.response

import org.sc.common.rest.Status
import org.sc.common.rest.TrailPreviewDto

data class TrailPreviewResponse (val status: Status,
                                 val messages: Set<String>,
                                 val content: List<TrailPreviewDto>) :
    RESTResponse()