package org.sc.service

import org.sc.common.rest.CustomItineraryRequestDto
import org.sc.common.rest.CustomItineraryResultDto
import org.sc.data.mapper.TrailMapper
import org.sc.manager.AccessibilityNotificationManager
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class CustomItineraryService @Autowired constructor(
    private val trailManager: TrailManager,
    private val trailMapper: TrailMapper,
    private val accessibilityNotificationManager: AccessibilityNotificationManager,
) {
    private val logger = Logger.getLogger(CustomItineraryService::class.java.name)
    fun calculateItinerary(customItinerary: CustomItineraryRequestDto): CustomItineraryResultDto {
        TODO("Not yet implemented")
    }





}