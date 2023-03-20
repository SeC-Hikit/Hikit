package org.sc.data.model

data class StaticTrailDetails(
    var pathGpx : String,
    var pathKml : String,
    var pathPdf : String
) {
    companion object {
        const val PATH_GPX = "pathGpx"
        const val PATH_KML = "pathKml"
        const val PATH_PDF = "pathPdf"
    }
}