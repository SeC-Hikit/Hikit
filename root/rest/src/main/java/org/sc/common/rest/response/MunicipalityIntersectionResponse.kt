package org.sc.common.rest.response

import org.sc.common.rest.MunicipalityToTrailDto
import org.sc.common.rest.Status
import org.sc.common.rest.TrailIntersectionDto

data class MunicipalityIntersectionResponse (val content: List<MunicipalityToTrailDto>)