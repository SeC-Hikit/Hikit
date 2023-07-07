package org.sc.job.import

import org.apache.logging.log4j.LogManager
import org.sc.manager.TrailManager
import org.sc.service.TrailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MunicipalityToTrailImportJob @Autowired constructor(
    private val trailService: TrailService,
    private val trailManager: TrailManager,
): PlaceImportJob {
    private val logger = LogManager.getLogger(MunicipalityToTrailImportJob::class.java)

    @Scheduled(cron = "0 */2 0-23 * * *")
    override fun import() {
        logger.info("Starting job to classify trails within municipalities administrations")
        val trailsWithoutMunicipalities = trailManager.getTrailsWithoutMunicipalities()
        trailsWithoutMunicipalities.forEach {
            val municipalities = trailService.findMunicipalityForTrailCoordinates(it.coordinates)
            if(municipalities.isNotEmpty()) {
                logger.info("Found municipalities for trail: ${it.code}. Going to update")
                it.municipalities = municipalities
                trailManager.update(it)
            }
        }
        logger.info("Done running municipalities import")
    }
}