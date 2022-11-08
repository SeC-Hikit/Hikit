package org.sc.service

import org.sc.common.rest.TrailPreviewDto
import org.sc.manager.TrailPreviewManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TrailPreviewService @Autowired constructor(private val trailPreviewManager: TrailPreviewManager) {
    fun searchByLocationNameOrTrailName(name: String, realm: String, isDraftTrailVisible: Boolean,
                                        skip: Int, limit: Int): List<TrailPreviewDto> =
            trailPreviewManager.searchByLocationNameOrName(name, realm, isDraftTrailVisible, skip, limit)

}