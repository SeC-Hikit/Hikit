package org.sc.common.rest

import java.util.*


data class TrailPreview constructor(val code: String,
                                    val classification: TrailClassification,
                                    val startPos : Position,
                                    val finalPos : Position,
                                    val date : Date)