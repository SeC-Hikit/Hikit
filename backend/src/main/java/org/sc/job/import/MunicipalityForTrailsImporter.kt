package org.sc.job.import

import org.openapitools.model.LineRequest
import org.sc.adapter.microservice.ErtMunicipalityMicroserviceAdapter
import org.sc.data.model.Coordinates
import org.sc.data.model.MunicipalityDetails
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MunicipalityForTrailsImporter @Autowired constructor(
    private val ertMunicipalityAdapter: ErtMunicipalityMicroserviceAdapter
) {
    private val logger = LoggerFactory.getLogger(MunicipalityForTrailsImporter::class.java)

    fun findMunicipalities(coordinates: List<Coordinates>): List<MunicipalityDetails> {
        val request = LineRequest().coordinates(coordinates.map {
            val coords = org.openapitools.model.Coordinates()
            coords.latitude(it.latitude)
            coords.longitude(it.longitude)
            coords
        })
        try {
            val findMunicipalitiesForLine = ertMunicipalityAdapter.findMunicipalitiesForLine(request)
            return findMunicipalitiesForLine!!.body!!.content.map { MunicipalityDetails(
                it.relatingCity.istat,
                it.relatingCity.city,
                it.relatingCity.province,
                it.relatingCity.provinceShort
            ) }
        } catch (exception : Exception) {
            logger.error("Something went wrong with retrieving/" +
                    "mapping municipality data from microservices: ${exception.message}")
        }
        return emptyList()
    }
}