package org.sc.common.rest.response

import org.sc.common.rest.Status

abstract class RESTResponse constructor(status: Status, messages : Set<String>)