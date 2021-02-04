package org.sc.common.rest.response

import org.sc.common.rest.MaintenanceDto
import org.sc.common.rest.Status

data class MaintenanceResponse (val status: Status,
                                val messages: Set<String>,
                                val maintenanceResponse: List<MaintenanceDto>,
) : RESTResponse()