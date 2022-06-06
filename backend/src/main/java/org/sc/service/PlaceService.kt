package org.sc.service

import org.sc.manager.PlaceManager
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(private val placeManager: PlaceManager,
                                          private val trailManager: TrailManager) {


}