package org.sc.service

import org.sc.configuration.ConfigurationProperties
import org.sc.configuration.ConfigurationProperties.LOCAL_IP_ADDRESS
import org.sc.data.helper.GsonBeanHelper
import org.sc.service.response.AltitudeApiResponse
import java.net.URL
import javax.inject.Inject
import javax.inject.Named

class AltitudeServiceHelper @Inject constructor(@Named(ConfigurationProperties.ALTITUDE_SERVICE_PROPERTY) altitudeServicePort: String,
                                                private val gsonBeanHelper: GsonBeanHelper) {

    private val pathToServiceApi: String = "$LOCAL_IP_ADDRESS:$altitudeServicePort/api/v1/lookup"

    fun getAltitudeByLongLat(latitude: Double,
                             longitude: Double): Double {
        val apiGetEndpoint = "http://$pathToServiceApi?locations=$latitude,$longitude"
        val getCall = URL(apiGetEndpoint).readText()
        val gsonBuilder: AltitudeApiResponse = gsonBeanHelper.gsonBuilder!!.fromJson(getCall, AltitudeApiResponse::class.java)
        return gsonBuilder.results.first().elevation
    }
}