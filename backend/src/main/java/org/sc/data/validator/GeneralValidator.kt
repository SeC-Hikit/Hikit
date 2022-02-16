package org.sc.data.validator

import org.sc.common.rest.*
import org.sc.common.rest.geo.GeoLineDto
import org.sc.common.rest.geo.RectangleDto
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
    private val linkedPlaceValidator: LinkedPlaceValidator,
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
    private val geoLineValidator: GeoLineValidator,
    private val accessibilityReportValidator: AccessibilityReportValidator
) {
    fun validate(acd: AccessibilityReportDto): Set<String> = accessibilityReportValidator.validate(acd)
    fun validate(acd: AccessibilityNotificationDto): Set<String> = accessibilityValidator.validate(acd)
    fun validate(tpl: LinkedPlaceDto): Set<String> = linkedPlaceValidator.validate(tpl)
    fun validate(cor: CoordinatesDto): Set<String> = coordinatesValidator.validate(cor)
    fun validate(cor: Coordinates2DDto): Set<String> = coordinatesValidator.validate(cor)
    fun validate(kv: KeyValueDto): Set<String> = keyValValidator.validate(kv)
    fun validate(lm: LinkedMediaDto): Set<String> = linkedMediaValidator.validate(lm)
    fun validate(md: MaintenanceDto): Set<String> = maintenanceValidator.validate(md)
    fun validate(fl: File): Set<String> = mediaFileValidator.validate(fl)
    fun validate(pld: PlaceRefDto): Set<String> = placeRefValidator.validate(pld)
    fun validate(pd: PlaceDto): Set<String> = placeValidator.validate(pd)
    fun validate(pgd: PointGeolocationDto): Set<String> = pointGeolocationValidatorDto.validate(pgd)
    fun validate(tcd: TrailCoordinatesDto): Set<String> = trailCoordinatesValidator.validate(tcd)
    fun validate(ti: TrailImportDto): Set<String> = trailImportValidator.validate(ti)
    fun validate(td: TrailDto): Set<String> = trailUpdateValidator.validate(td)
    fun validate(poi: PoiDto): Set<String> = poiValidator.validate(poi)
    fun validate(geoLine: GeoLineDto): Set<String> = geoLineValidator.validate(geoLine)

    fun validateFileName(fn: String): Set<String> = fileNameValidator.validate(fn)

    fun validateAcc(id: String): Set<String> = accessibilityValidator.validateUpdateRequest(id)
    fun validateReportAcc(id: String): Set<String> = accessibilityReportValidator.validateUpdateRequest(id)
    fun validateUpdateMedia(id: String): Set<String> = mediaExistenceValidator.validateDeleteRequest(id)
    fun validateUpdatePlace(id: String): Set<String> = placeExistenceValidator.validatePlace(id)
    fun validateUpdatePoi(id: String): Set<String> = poiValidator.validateExistenceAndAuth(id)
    fun validateUpdateTrail(id: String): Set<String> = trailExistenceValidator.validateExistenceAndRealm(id)
    fun validateUpdateMaintenance(id: String): Set<String> = maintenanceValidator.validateExistenceAndRealm(id)

    fun validateMediaExistence(id: String): Set<String> = mediaExistenceValidator.validate(id)
    fun validatePoiExistence(id: String): Set<String> = poiExistenceValidator.validate(id)
    fun validate(rectangleDto: RectangleDto): Set<String> = rectangleValidator.validate(rectangleDto)
}