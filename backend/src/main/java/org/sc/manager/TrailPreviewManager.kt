package org.sc.manager

import org.sc.common.rest.TrailMappingDto
import org.sc.common.rest.TrailPreviewDto
import org.sc.data.mapper.TrailMappingMapper
import org.sc.data.mapper.TrailPreviewMapper
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
    ): List<TrailPreviewDto> =
        trailDAO.getTrailPreviews(skip, limit, realm, isDraftTrailVisible)
                .map { trailPreviewMapper.map(it) }

    fun findPreviewsByCode(
            code: String,
            skip: Int,
            limit: Int,
            realm: String,
            isDraftTrailVisible: Boolean
    ): List<TrailPreviewDto> =
            trailDAO.findPreviewsByCode(code, skip, limit, realm, isDraftTrailVisible).map { trailPreviewMapper.map(it) }

    fun getRawPreviews(skip: Int, limit: Int): List<TrailPreviewDto> =
        trailRawDAO.get(skip, limit).map { trailPreviewMapper.map(it) }

    fun getPreviewById(id: String): List<TrailPreviewDto> = trailDAO.trailPreviewById(id)
        .map { trailPreviewMapper.map(it) }

    fun countPreview(): Long = trailDAO.countTrail()
    fun countPreviewByRealm(realm: String, isDraftTrailVisible: Boolean): Long =
            trailDAO.countTrailByRealm(realm, isDraftTrailVisible)
    fun countFindingByCode(realm: String, code: String, isDraftTrailVisible: Boolean): Long =
            trailDAO.countTotalByCode(realm, code, isDraftTrailVisible)
    fun countRaw(): Long = trailRawDAO.count()
}