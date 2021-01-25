package org.sc.common.rest

import java.util.*


data class TrailPreviewDto constructor(val code: String,
                                       val classification: TrailClassification,
                                       val startPos : PositionDto,
                                       val finalPos : PositionDto,
                                       val date : Date)