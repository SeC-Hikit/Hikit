package org.sc.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.sc.configuration.AppProperties
import org.sc.configuration.AppProperties.LOCAL_IP_ADDRESS
import org.sc.data.model.Coordinates2D
import org.sc.service.response.AltitudeApiRequestPoint
import org.sc.service.response.AltitudeApiResponse
import org.sc.service.response.AltitudeServiceRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

val ALTITUDE_CALL_RETRIES = 3
val ALTITUDE_CALL_CHUNK_SIZE = 100

@Service
class AltitudeServiceAdapter @Autowired constructor(appProperties: AppProperties,
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

    fun getAltituteByLongLat(coordinates: List<Pair<Double, Double>>): List<Double> {

        val coordinatesChunks = coordinates.chunked(ALTITUDE_CALL_CHUNK_SIZE)

        val result : MutableList<Double> = mutableListOf()

        for(chunk in coordinatesChunks) {

           val coordinateAltituideList = callAltitudeWithExponentialBackoff(chunk, ALTITUDE_CALL_RETRIES)

            if(chunk.size == coordinateAltituideList.size) {
                result.addAll(coordinateAltituideList)
            } else {
                //in case of error the result contains the same number of elements of the input
                result.addAll(MutableList(coordinates.size) { 0.0 })
            }
        }

        return result
    }

    private fun callAltitudeWithExponentialBackoff(coordinates: List<Pair<Double, Double>>, retry : Int) : List<Double> {

        val postData: ByteArray = buildAltitudeRequest(coordinates)

        var retryCounter : Int = 1
        while(retryCounter <= retry) {

            try {
                val connection = buildAltitudeRequestConnection(postData.size)
                val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)

                outputStream.write(postData)
                outputStream.flush()

                if(connection.responseCode == HttpURLConnection.HTTP_OK) {

                    val inputStream: DataInputStream = DataInputStream(connection.inputStream)
                    val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val output: String = reader.readLine()

                    val gsonBuilder: AltitudeApiResponse = objectMapper.readValue(output, AltitudeApiResponse::class.java)
                    return gsonBuilder.results.map { elem -> elem.elevation }

                } else {
                    retryCounter++
                    TimeUnit.SECONDS.sleep((retryCounter * retryCounter * 1L))
                }
            } catch (exception: Exception) {
                retryCounter++
                TimeUnit.SECONDS.sleep((retryCounter * retryCounter * 1L))
            }
        }

        return listOf();
    }

    private fun buildAltitudeRequestConnection(contentSize : Int) : HttpURLConnection {

        val apiGetEndpoint = "http://$pathToServiceApi"
        val getCall = URL(apiGetEndpoint)

        val connection = getCall.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-length", contentSize.toString())
        connection.setRequestProperty("Content-Type", "application/json")

        return connection
    }

    private fun buildAltitudeRequest(coordinates: List<Pair<Double, Double>>): ByteArray {
        val requestObject = AltitudeServiceRequest(coordinates.map { elem -> AltitudeApiRequestPoint(elem.first, elem.second) })
        val requestMessage = objectMapper.writeValueAsString(requestObject)
        return requestMessage.toByteArray(StandardCharsets.UTF_8)
    }
}