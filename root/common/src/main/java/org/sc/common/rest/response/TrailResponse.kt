package org.sc.common.rest.response

import org.sc.common.rest.Status
import org.sc.common.rest.TrailDto

data class TrailResponse (val status: Status,
                          val messages: Set<String>,
                          val trails: List<TrailDto>) :
    RESTResponse()