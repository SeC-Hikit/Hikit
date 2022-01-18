package org.sc.manager

import org.sc.common.rest.TrailPreviewDto
import org.sc.data.mapper.TrailPreviewMapper
import org.sc.data.repository.TrailDAO
import org.sc.data.repository.TrailRawDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailPreviewManager @Autowired constructor(
    private val trailPreviewMapper: TrailPreviewMapper,
    private val trailDAO: TrailDAO,
    private val trailRawDAO: TrailRawDAO
) {

    fun getPreviews(
        skip: Int,
        limit: Int,
        realm: String
    ): List<TrailPreviewDto> =
        trailDAO.getTrailPreviews(skip, limit, realm).map { trailPreviewMapper.map(it) }

    fun findPreviewsByCode(
            code: String,
            skip: Int,
            limit: Int,
            realm: String
    ): List<TrailPreviewDto> =
            trailDAO.findPreviewsByCode(code, skip, limit, realm).map { trailPreviewMapper.map(it) }

    fun getRawPreviews(skip: Int, limit: Int): List<TrailPreviewDto> =
        trailRawDAO.get(skip, limit).map { trailPreviewMapper.map(it) }

    fun getPreviewById(id: String): List<TrailPreviewDto> = trailDAO.trailPreviewById(id)
        .map { trailPreviewMapper.map(it) }

    fun countPreview(): Long = trailDAO.countTrail()
    fun countFindingByCode(code: String): Long = trailDAO.countTotalByCode(code)
    fun countRaw(): Long = trailRawDAO.count()
}