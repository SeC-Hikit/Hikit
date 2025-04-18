package org.sc.adapter.microservice

import org.ert.api.cai2hikit.TrailApi
import org.openapitools.model.Trail
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException

@Component
class CaiToHikitMicroserviceAdapter @Autowired constructor(
        private val restTemplateBuilder: RestTemplateBuilder
    ) : TrailApi {

    private val logger = LoggerFactory.getLogger(CaiToHikitMicroserviceAdapter::class.java)

    @Value("\${microservice.cai2hikit.endpoint:http://localhost:8992/api/v1}")
    lateinit var endpointUrl: String

    override fun getTrail(code: String) : ResponseEntity<Trail>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "/api/v1/trail/code/${code}"
                    ),
                    org.openapitools.model.Trail::class.java
                )
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote CAI2Hikit microservice endpoint /code responded with an error",
                restClientException.cause
            )
            null
        }
    }

    @Suppress(("UNCHECKED_CAST"))
    override fun getTrailByRef(ref : String) : ResponseEntity<List<Trail>>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "/api/v1/trail/ref/${ref}"
                    ),
                    List::class.java
                ) as ResponseEntity<List<Trail>>
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote CAI2Hikit microservice endpoint /ref responded with an error",
                restClientException.cause
            )
            null
        }
    }

    @Suppress(("UNCHECKED_CAST"))
    override fun matchTrail() : ResponseEntity<List<Trail>>? {
        return try {
            restTemplateBuilder.build()
                .getForEntity(
                    endpointUrl.plus(
                        "/api/v1/trail/match"
                    ),
                    List::class.java
                ) as ResponseEntity<List<Trail>>
        } catch (restClientException: RestClientException) {
            logger.error(
                "The remote CAI2Hikit microservice endpoint /match trail responded with an error",
                restClientException.cause
            )
            null
        }
    }
}