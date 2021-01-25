package org.sc.data.entity

import org.sc.common.rest.TrailClassification
import java.util.*


data class TrailPreview constructor(val code: String,
                                    val classification: TrailClassification,
                                    val startPos : Position,
                                    val finalPos : Position,
                                    val date : Date)