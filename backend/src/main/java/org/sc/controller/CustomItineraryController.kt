package org.sc.controller

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.CustomItineraryRequestDto
import org.sc.common.rest.CustomItineraryResultDto
import org.sc.data.validator.GeneralValidator
import org.sc.service.CustomItineraryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping(CustomItineraryController.PREFIX)
class CustomItineraryController constructor(
    private val generalValidator: GeneralValidator,
    private val customItineraryService: CustomItineraryService,
) {

    companion object {
        const val PREFIX = "/custom-itinerary"
    }

    @Operation(summary = "Calculate an itinerary based on the provided set of line segments")
    @PostMapping("/construct")
    fun calculate(@RequestBody customItinerary: CustomItineraryRequestDto): CustomItineraryResultDto {
        val errors: Set<String> = generalValidator.validate(customItinerary.geoLineDto)
        if (errors.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Not valid geoline request"
            )
        }
        return customItineraryService.calculateItinerary(customItinerary)
    }

    @Operation(summary = "Download a gpx itinerary based on the calculated result")
    @PostMapping("/itinerary-download")
    fun downloadGpx(@RequestBody customItinerary: CustomItineraryResultDto): ByteArray {
        return customItineraryService.exportGpx(customItinerary)
    }

}