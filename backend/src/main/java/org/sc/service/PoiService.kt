package org.sc.service

import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PoiService @Autowired constructor(private val trailManager: TrailManager) {
}