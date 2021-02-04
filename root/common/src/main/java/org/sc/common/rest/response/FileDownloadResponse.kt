package org.sc.common.rest.response

import org.sc.common.rest.Status

data class FileDownloadResponse (val status: Status,
                                 val message: Set<String>,
                                 val path: String) : RESTResponse()