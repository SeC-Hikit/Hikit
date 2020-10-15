package org.sc.service

import org.sc.configuration.AppProperties
import org.sc.configuration.ConfigurationProperties.LOCAL_IP_ADDRESS
import org.sc.data.helper.GsonBeanHelper
import org.sc.service.response.AltitudeApiResponse
import java.net.URL
import javax.inject.Inject

class AltitudeServiceHelper @Inject constructor(appProperties: AppProperties,
                                                private val gsonBeanHelper: GsonBeanHelper) {

    private val portToAltitudeService : Int = appProperties.altitudeServicePort
    private val pathToServiceApi: String = "$LOCAL_IP_ADDRESS:$portToAltitudeService/api/v1/lookup"

    fun getAltitudeByLongLat(latitude: Double,
                             longitude: Double): Double {
        val apiGetEndpoint = "http://$pathToServiceApi?locations=$latitude,$longitude"
        val getCall = URL(apiGetEndpoint).readText()
        val gsonBuilder: AltitudeApiResponse = gsonBeanHelper.gsonBuilder!!.fromJson(getCall, AltitudeApiResponse::class.java)
        return gsonBuilder.results.first().elevation
    }
}