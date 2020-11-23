package org.sc.common.rest.controller

import java.util.*


data class TrailPreview constructor(val code: String,
                                    val classification: TrailClassification,
                                    val startPos : Position,
                                    val endPos : Position,
                                    val date : Date)