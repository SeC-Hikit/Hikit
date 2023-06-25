package org.sc.service

import org.sc.common.rest.TrailPreviewDto
import org.sc.data.mapper.TrailPreviewMapper
import org.sc.manager.TrailManager
import org.sc.manager.TrailPreviewManager
import org.sc.processor.TrailExporter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TrailPreviewService @Autowired constructor(
    private val trailPreviewManager: TrailPreviewManager,
    private val trailManager: TrailManager,
    private val trailPreviewMapper: TrailPreviewMapper,
    private val trailExporter: TrailExporter
) {
    fun getTrailPreviews(skip: Int, limits: Int, realm: String, isDraftTrailVisible: Boolean): List<TrailPreviewDto> =
        trailPreviewManager.getPreviews(skip, limits, realm, isDraftTrailVisible).map { trailPreviewMapper.map(it) }


    fun exportList(realm: String): ByteArray {
        val allPreviews = trailPreviewManager.getPreviews(
            0, Integer.MAX_VALUE,
            realm, true
        )
        return trailExporter.exportToCsv(allPreviews)
    }

    fun searchByLocationNameOrTrailName(
        name: String, realm: String, isDraftTrailVisible: Boolean,
        skip: Int, limit: Int
    ): List<TrailPreviewDto> =
        trailPreviewManager.searchByLocationNameOrCode(name, realm, isDraftTrailVisible, skip, limit)

}