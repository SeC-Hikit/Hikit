package org.sc.adapter.microservice

import org.ert.api.LocalityApi
import org.openapitools.model.LocalityResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException

@Component
class ErtLocalityMicroserviceAdapter @Autowired constructor(
    private val restTemplateBuilder: RestTemplateBuilder
) : LocalityApi {
    private val logger = LoggerFactory.getLogger(ErtLocalityMicroserviceAdapter::class.java)

    @Value("\${microservice.ert.localities:http://localhost:8991/api/v1/locality}")
    lateinit var endpointUrl: String

    override fun getByIstat(
        istat: String
    ): ResponseEntity<LocalityResponse>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "/${istat}"
                    ),
                    org.openapitools.model.LocalityResponse::class.java
                )
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote ERT microservice endpoint locality responded with an error",
                restClientException.cause
            )
            null
        }
    }


    override fun get(
        latitude: Double?,
        longitude: Double?,
        distance: Double?,
        skip: Int?,
        limit: Int?
    ): ResponseEntity<LocalityResponse>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "?latitude=${latitude}" +
                                "&longitude=${longitude}" +
                                "&distance=${distance}" +
                                "&skip=${skip}" +
                                "&limit=${limit}"
                    ),
                    org.openapitools.model.LocalityResponse::class.java
                )
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote ERT microservice endpoint locality responded with an error",
                restClientException.cause
            )
            null
        }
    }
}