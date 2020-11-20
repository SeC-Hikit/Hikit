package org.sc.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.sc.common.config.ConfigurationProperties.LOCAL_IP_ADDRESS
import org.sc.common.rest.controller.helper.ObjectMapperWrapper
import org.sc.configuration.AppProperties
import org.sc.service.response.AltitudeApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URL

@Service
class AltitudeServiceHelper @Autowired constructor(appProperties: AppProperties,
                                                   private val objectMapper: ObjectMapper) {

    private val portToAltitudeService : Int = appProperties.altitudeServicePort
    private val pathToServiceApi: String = "$LOCAL_IP_ADDRESS:$portToAltitudeService/api/v1/lookup"

    fun getAltitudeByLongLat(latitude: Double,
                             longitude: Double): Double {
        val apiGetEndpoint = "http://$pathToServiceApi?locations=$latitude,$longitude"
        val getCall = URL(apiGetEndpoint).readText()
        val gsonBuilder: AltitudeApiResponse = objectMapper.readValue(getCall, AltitudeApiResponse::class.java)
        return gsonBuilder.results.first().elevation
    }
}