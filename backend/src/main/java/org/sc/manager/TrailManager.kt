package org.sc.manager

import org.sc.common.rest.*
import org.sc.common.rest.geo.RectangleDto
import org.sc.data.entity.mapper.StaticTrailDetailsMapper
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.geo.TrailPlacesAligner
import org.sc.data.mapper.*
import org.sc.data.model.Place
import org.sc.data.model.StaticTrailDetails
import org.sc.data.model.Trail
import org.sc.data.model.TrailPreview
import org.sc.data.repository.PlaceDAO
import org.sc.data.repository.TrailDAO
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailManager @Autowired constructor(
    private val trailDAO: TrailDAO,
    private val placeDAO: PlaceDAO,
    private val trailMapper: TrailMapper,
    private val linkedMediaMapper: LinkedMediaMapper,
    private val placeRefMapper: PlaceRefMapper,
    private val trailMappingMapper: TrailMappingMapper,
    private val trailPlacesAligner: TrailPlacesAligner,
    private val staticTrailDetailsMapper: StaticTrailDetailsMapper
) {

    fun get(
            page: Int,
            count: Int,
            trailSimplifierLevel: TrailSimplifierLevel,
            realm: String,
            isDraftTrailVisible: Boolean,
    ): List<TrailDto> = trailDAO.getTrails(page, count, trailSimplifierLevel, realm, isDraftTrailVisible)
            .map { trailMapper.map(it) }

    fun getById(id: String, level: TrailSimplifierLevel): List<TrailDto> =
            trailDAO.getTrailById(id, level).map { trailMapper.map(it) }

    fun getByIds(id: String, level: TrailSimplifierLevel): List<TrailDto> =
            trailDAO.getTrailById(id, level).map { trailMapper.map(it) }

    fun getByPlaceRefId(code: String, page: Int, limit: Int, level: TrailSimplifierLevel): List<TrailDto> =
            trailDAO.getTrailByPlaceId(code, page, limit, level).map { trailMapper.map(it) }

    fun deleteById(id: String): List<TrailDto> {
        propagateChangesToTrails(id)
        val deletedTrailInMem = trailDAO.delete(id)
        return deletedTrailInMem.map { trailMapper.map(it) }
    }

    fun propagateChangesToTrails(trailId: String) {
        val trail = getPreviewById(trailId).first()
        trail.locations.forEach {
            trailDAO.propagatePlaceRemovalFromRefs(it.placeId, trail.id)
        }
    }

    fun save(trail: Trail): List<TrailDto> {
        return trailDAO.upsert(trail).map { trailMapper.map(it) }
    }

    fun update(trail: Trail): List<TrailDto> {
        return trailDAO.update(trail).map { trailMapper.map(it) }
    }

    fun updateTrailPlaceNamesReference(trailId: String, placeId: String, placeName: String): List<TrailDto> {
        return trailDAO.updateTrailNamePlaceReference(trailId, placeId, placeName).map { trailMapper.map(it) }
    }

    fun linkMedia(id: String, linkedMediaRequest: LinkedMediaDto): List<TrailDto> {
        val linkMedia = linkedMediaMapper.map(linkedMediaRequest)
        val result = trailDAO.linkMedia(id, linkMedia)
        return result.map { trailMapper.map(it) }
    }

    fun unlinkMedia(id: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto): List<TrailDto> {
        val unlinkedTrail = trailDAO.unlinkMedia(id, unLinkeMediaRequestDto.id)
        return unlinkedTrail.map { trailMapper.map(it) }
    }

    fun doesTrailExist(id: String): Boolean = trailDAO.getTrailById(id, TrailSimplifierLevel.LOW).isNotEmpty()

    fun linkTrailToPlace(targetTrailId: String, placeRef: PlaceRefDto): List<TrailDto> {

        val isPlaceAlreadyInTrail = trailDAO.getTrailPreviewById(targetTrailId).first().locations.map { it.placeId }.contains(placeRef.placeId)
        if (!isPlaceAlreadyInTrail) {
            trailDAO.linkGivenTrailToPlace(targetTrailId, placeRefMapper.map(placeRef))
        }
        val linkedPlace = placeDAO.linkTrailToPlace(placeRef.placeId, targetTrailId, placeRef.coordinates)
        val place = linkedPlace.first()
        ensureLinkingTrailToExistingCrosswayReferences(place, targetTrailId)
        ensureCreatingNewCrosswayReferences(place, targetTrailId, placeRef)

        return getById(targetTrailId, TrailSimplifierLevel.LOW)
    }

    private fun ensureLinkingTrailToExistingCrosswayReferences(place: Place, targetTrailId: String) {
        trailDAO.linkAllExistingTrailConnectionWithNewTrailId(place.id, targetTrailId)
    }

    fun getByMatchingStartEndPoint(startPos: TrailCoordinatesDto, finalPos: TrailCoordinatesDto): List<TrailMappingDto> =
            trailDAO.getByStartEndPoint(startPos.latitude, startPos.longitude, finalPos.latitude, finalPos.longitude).map { trailMappingMapper.map(it) }


    private fun ensureCreatingNewCrosswayReferences(place: Place,
                                                    trailId: String,
                                                    placeRef: PlaceRefDto) {
        place.crossingTrailIds.filter { trailId != it }
                .forEach { otherCrossingTrailId ->
                    linkAllNewTrailConnectionsWithNewTrailId(otherCrossingTrailId, trailId, placeRef)
                }
    }

    private fun linkAllNewTrailConnectionsWithNewTrailId(otherCrossingTrailId: String,
                                                         targetTrailId: String,
                                                         placeRef: PlaceRefDto) {
        val previewById = getPreviewById(otherCrossingTrailId)
        val first = previewById.first()
        val encounteredTrailIds = first.locations.flatMap { it.encounteredTrailIds }

        val isTargetTrailNotPresentInListOfEncounteredTrails = !encounteredTrailIds.contains(targetTrailId)

        if (isTargetTrailNotPresentInListOfEncounteredTrails) {
            val byId = getById(otherCrossingTrailId, TrailSimplifierLevel.HIGH)
            if (byId.isEmpty()) {
                throw IllegalStateException()
            }
            val targetTrail = byId.first()

            val targetPlacesRefs = targetTrail.locations.plus(placeRef)

            val reorderedPlaces = trailPlacesAligner.sortLocationsByTrailCoordinatesDto(
                    targetTrail.coordinates, targetPlacesRefs)

            trailDAO.updatePlacesRefsByTrailId(otherCrossingTrailId, reorderedPlaces)
        }
    }

    fun unlinkPlace(id: String, placeRef: PlaceRefDto): List<TrailDto> {
        val unLinkPlace = trailDAO.unLinkPlace(id, placeRefMapper.map(placeRef))
        return unLinkPlace.map { trailMapper.map(it) }
    }

    fun count(): Long = trailDAO.countTrail()

    fun removePlaceRefFromTrails(placeId: String) {
        trailDAO.unlinkPlaceFromAllTrails(placeId)
    }

    fun findTrailsWithinRectangle(
            rectangleDto: RectangleDto,
            level: TrailSimplifierLevel,
            isDraftTrailVisible: Boolean
    ): List<TrailDto> {
        val trails = trailDAO.findTrailsWithinGeoSquare(
                CoordinatesRectangle(rectangleDto.bottomLeft, rectangleDto.topRight), 0, 100, level, isDraftTrailVisible)
        return trails.map { trailMapper.map(it) }
    }

    fun findTrailMappingsWithinRectangle(rectangleDto: RectangleDto): List<TrailMappingDto> {
        val trailMappings = trailDAO.findTrailMappingWithinGeoSquare(
                CoordinatesRectangle(rectangleDto.bottomLeft, rectangleDto.topRight), 0, 100)
        return trailMappings.map { trailMappingMapper.map(it) }
    }

    fun getCodesByTrailIds(ids: List<String>) = trailDAO.getCodesById(ids)

    private fun getPreviewById(id: String): List<TrailPreview> =
            trailDAO.getTrailPreviewById(id)

    fun updateTrailPlaceReferences(oldId: String, id: String, name: String) {
        trailDAO.updateAllPlaceReferencesWithNewPlaceId(oldId, id, name)
    }

    fun updateStaticResources(id: String, resources: StaticTrailDetails) {
        trailDAO.updateStaticResources(id, staticTrailDetailsMapper.mapToDocument(resources))
    }

}

