package org.sc.common.rest.controller


data class TrailPreview constructor(val code: String,
                                    val classification: TrailClassification,
                                    val startPos : Position,
                                    val endPos : Position)