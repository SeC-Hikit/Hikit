package org.sc.manager

import org.sc.common.rest.TrailMappingDto
import org.sc.common.rest.TrailPreviewDto
import org.sc.data.mapper.TrailMappingMapper
import org.sc.data.mapper.TrailPreviewMapper
import org.sc.data.model.TrailPreview
import org.sc.data.repository.TrailDAO
import org.sc.data.repository.TrailRawDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailPreviewManager @Autowired constructor(
    private val trailPreviewMapper: TrailPreviewMapper,
    private val trailMappingMapper: TrailMappingMapper,
    private val trailDAO: TrailDAO,
    private val trailRawDAO: TrailRawDAO
) {

    fun getMappings(
        skip: Int,
        limit: Int,
        realm: String,
        isDraftTrailVisible: Boolean
    ): List<TrailMappingDto> =
        trailDAO.getTrailsMappings(skip, limit, realm, isDraftTrailVisible)
            .map { trailMappingMapper.map(it) }

    fun getPreviews(
        skip: Int,
        limit: Int,
        realm: String,
        isDraftTrailVisible: Boolean
    ): List<TrailPreview> =
        trailDAO.getTrailPreviews(skip, limit, realm, isDraftTrailVisible)

    fun findPreviewsByMunicipality(
        municipality: String,
        skip: Int,
        limit: Int,
        realm: String,
        isDraftTrailVisible: Boolean
    ):
            List<TrailPreviewDto> {
        return trailDAO.findByMunicipality(
            municipality, realm,
            isDraftTrailVisible, skip, limit
        )
            .map { trailPreviewMapper.map(it) }
    }

    fun findPreviewsByCode(
        code: String,
        skip: Int,
        limit: Int,
        realm: String,
        isDraftTrailVisible: Boolean
    ): List<TrailPreviewDto> =
        trailDAO.findPreviewsByCode(code, skip, limit, realm, isDraftTrailVisible).map { trailPreviewMapper.map(it) }

    fun searchByLocationNameOrCode(
        name: String,
        realm: String,
        isDraftTrailVisible: Boolean,
        skip: Int,
        limit: Int
    ): List<TrailPreviewDto> {
        return trailDAO.searchByLocationOrTrailNameCode(
            name, realm,
            isDraftTrailVisible, skip, limit
        )
            .map { trailPreviewMapper.map(it) }
    }

    fun getRawPreviews(skip: Int, limit: Int, realm: String): List<TrailPreviewDto> =
        trailRawDAO.get(skip, limit, realm).map { trailPreviewMapper.map(it) }

    fun getPreviewById(id: String): List<TrailPreviewDto> = trailDAO.trailPreviewById(id)
        .map { trailPreviewMapper.map(it) }

    fun countPreview(): Long = trailDAO.countTrail()
    fun countPreviewByRealm(realm: String, isDraftTrailVisible: Boolean): Long =
        trailDAO.countTrailByRealm(realm, isDraftTrailVisible)

    fun countFindingByCode(realm: String, code: String, isDraftTrailVisible: Boolean): Long =
        trailDAO.countTotalByCode(realm, code, isDraftTrailVisible)

    fun countFindingByNameOrLocationName(name: String, realm: String, isDraftTrailVisible: Boolean): Long =
        trailDAO.countFindingByNameOrLocationName(name, realm, isDraftTrailVisible)

    fun countRaw(realm: String): Long = trailRawDAO.count(realm)
    fun countFindingByMunicipality(realm: String, municipality: String, isDraftTrailVisible: Boolean): Long {
        return trailDAO.countByMunicipality(municipality, realm, isDraftTrailVisible)
    }
}