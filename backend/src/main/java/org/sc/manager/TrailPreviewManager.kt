package org.sc.manager

import org.sc.common.rest.TrailPreviewDto
import org.sc.data.mapper.TrailPreviewMapper
import org.sc.data.repository.TrailDAO
import org.sc.data.repository.TrailRawDAO

class TrailPreviewManager constructor(
    private val trailPreviewMapper: TrailPreviewMapper,
    private val trailDAO: TrailDAO,
    private val trailRawDAO: TrailRawDAO
) {

    fun getPreviews(skip: Int, limit: Int): List<TrailPreviewDto> =
        trailDAO.getTrailPreviews(skip, limit).map { trailPreviewMapper.map(it) }

    fun getRawPreviews(skip: Int, limit: Int): List<TrailPreviewDto> {
        val trailPreviews = trailDAO.getTrailPreviews(skip, limit).map { trailPreviewMapper.map(it) }
        val trailPreviewRaws = trailRawDAO.get(skip, limit).map { trailPreviewMapper.map(it) }
        return trailPreviews.union(trailPreviewRaws).toList()
    }

    fun getPreviewById(id: String): List<TrailPreviewDto> = trailDAO.trailPreviewById(id)
        .map { trailPreviewMapper.map(it) }

    fun countRawAndTrail(): Long = countPreview() + countRaw()
    fun countPreview(): Long = trailDAO.countTrail()
    fun countRaw(): Long = trailRawDAO.count()
}