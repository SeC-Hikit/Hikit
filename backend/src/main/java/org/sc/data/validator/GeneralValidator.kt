package org.sc.data.validator

import org.sc.common.rest.*
import org.sc.common.rest.geo.RectangleDto
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.data.validator.poi.PoiExistenceValidator
import org.sc.data.validator.poi.PoiValidator
import org.sc.data.validator.trail.TrailExistenceValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class GeneralValidator @Autowired constructor(
    private val accessibilityValidator: AccessibilityValidator,
    private val coordinatesValidator: CoordinatesValidator,
    private val fileNameValidator: FileNameValidator,
    private val keyValValidator: KeyValValidator,
    private val linkedMediaValidator: LinkedMediaValidator,
    private val maintenanceValidator: MaintenanceValidator,
    private val mediaExistenceValidator: MediaExistenceValidator,
    private val mediaFileValidator: MediaFileValidator,
    private val placeExistenceValidator: PlaceExistenceValidator,
    private val placeRefValidator: PlaceRefValidator,
    private val placeValidator: PlaceValidator,
    private val pointGeolocationValidatorDto: PointGeolocationValidatorDto,
    private val trailCoordinatesValidator: TrailCoordinatesValidator,
    private val trailImportValidator: TrailImportValidator,
    private val trailUpdateValidator: TrailUpdateValidator,
    private val trailExistenceValidator: TrailExistenceValidator,
    private val poiExistenceValidator: PoiExistenceValidator,
    private val poiValidator: PoiValidator,
    private val rectangleValidator: RectangleValidator,
) {
    fun validate(acd: AccessibilityNotificationCreationDto): Set<String> = accessibilityValidator.validate(acd)
    fun validate(cor: CoordinatesDto): Set<String> = coordinatesValidator.validate(cor)
    fun validate(kv: KeyValueDto): Set<String> = keyValValidator.validate(kv)
    fun validate(lm: LinkedMediaDto): Set<String> = linkedMediaValidator.validate(lm)
    fun validate(md: MaintenanceCreationDto): Set<String> = maintenanceValidator.validate(md)
    fun validate(fl: File): Set<String> = mediaFileValidator.validate(fl)
    fun validate(pld: PlaceRefDto): Set<String> = placeRefValidator.validate(pld)
    fun validate(pd: PlaceDto): Set<String> = placeValidator.validate(pd)
    fun validate(pgd: PointGeolocationDto): Set<String> = pointGeolocationValidatorDto.validate(pgd)
    fun validate(tcd: TrailCoordinatesDto): Set<String> = trailCoordinatesValidator.validate(tcd)
    fun validate(ti: TrailImportDto): Set<String> = trailImportValidator.validate(ti)
    fun validate(td: TrailDto): Set<String> = trailUpdateValidator.validate(td)
    fun validate(poi: PoiDto): Set<String> = poiValidator.validate(poi)

    fun validateFileName(fn: String): Set<String> = fileNameValidator.validate(fn)

    fun validateDeleteAcc(id: String): Set<String> = accessibilityValidator.validateDeleteRequest(id)
    fun validateDeleteMedia(id: String): Set<String> = mediaExistenceValidator.validateDeleteRequest(id)

    fun validatePlaceExistence(id: String): Set<String> = placeExistenceValidator.validate(id)
    fun validateMediaExistence(id: String): Set<String> = mediaExistenceValidator.validate(id)
    fun validateTrailExistence(id: String): Set<String> = trailExistenceValidator.validate(id)
    fun validatePoiExistence(id: String): Set<String> = poiExistenceValidator.validate(id)
    fun validate(rectangleDto: RectangleDto): Set<String> = rectangleValidator.validate(rectangleDto)
}