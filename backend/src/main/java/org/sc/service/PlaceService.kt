package org.sc.service

import org.sc.common.rest.PlaceDto
import org.sc.manager.PlaceManager
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(private val placeManager: PlaceManager,
                                          private val trailManager: TrailManager) {


    fun fetchPaginated(skip: Int, limit: Int, realm: String): List<PlaceDto> =
            placeManager.getPaginated(skip, limit, realm, true)

    fun countByRealm(realm: String): Long =
            placeManager.countByRealm(realm)


}