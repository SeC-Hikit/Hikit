package org.sc.controller

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.CustomItineraryRequestDto
import org.sc.common.rest.CustomItineraryResultDto
import org.sc.data.validator.GeneralValidator
import org.sc.service.CustomItineraryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ResponseStatusException

class CustomItineraryController constructor(
    private val generalValidator: GeneralValidator,
    private val customItineraryService: CustomItineraryService,
) {

    @Operation(summary = "Calculate an itinerary based on the provided set of line segments")
    @PostMapping("/construct-itinerary")
    fun calculate(@RequestBody customItinerary: CustomItineraryRequestDto): CustomItineraryResultDto {
        val errors: Set<String> = generalValidator.validate(customItinerary.coordinates)
        if (errors.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Not valid geoline request"
            )
        }
        return customItineraryService.calculateItinerary(customItinerary)
    }
}