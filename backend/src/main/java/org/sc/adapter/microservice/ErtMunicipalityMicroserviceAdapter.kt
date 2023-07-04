package org.sc.adapter.microservice

import org.ert.api.MunicipalityApi
import org.openapitools.model.LineRequest
import org.openapitools.model.MunicipalityResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException

@Component
class ErtMunicipalityMicroserviceAdapter @Autowired constructor(
    private val restTemplateBuilder: RestTemplateBuilder
) : MunicipalityApi {
    private val logger = LoggerFactory.getLogger(ErtMunicipalityMicroserviceAdapter::class.java)

    @Value("\${microservice.ert.localities:http://localhost:8991/api/v1/municipality}")
    lateinit var endpointUrl: String

    override fun getByName(
        name: String
    ): ResponseEntity<MunicipalityResponse>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "/name/${name}"
                    ),
                    org.openapitools.model.MunicipalityResponse::class.java
                )
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote ERT microservice endpoint municipality responded " +
                        "with an error, for GET /name",
                restClientException.cause
            )
            null
        }
    }

    override fun findMunicipalitiesForLine(line: LineRequest): ResponseEntity<MunicipalityResponse>? {
        return try {
            restTemplateBuilder.build()
                .postForEntity(
                    endpointUrl, line,
                    org.openapitools.model.MunicipalityResponse::class.java
                )
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote ERT microservice endpoint municipality responded with an error, " +
                        "for line intersection POST request",
                restClientException.cause
            )
            null
        }
    }

}