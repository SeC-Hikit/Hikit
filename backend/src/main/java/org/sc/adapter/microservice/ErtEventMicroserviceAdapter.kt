package org.sc.adapter.microservice

import org.ert.api.EventApi
import org.openapitools.model.EventResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException

@Component
class ErtEventMicroserviceAdapter @Autowired constructor(
    private val restTemplateBuilder: RestTemplateBuilder
) : EventApi {
    private val logger = LoggerFactory.getLogger(ErtEventMicroserviceAdapter::class.java)

    @Value("\${microservice.ert.event:http://localhost:8991/api/v1/event}")
    lateinit var endpointUrl: String

    override fun getByIstat1(
        istat: String
    ): ResponseEntity<EventResponse>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "/${istat}"
                    ),
                    org.openapitools.model.EventResponse::class.java
                )
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote ERT microservice endpoint event responded with an error",
                restClientException.cause
            )
            null
        }
    }
}