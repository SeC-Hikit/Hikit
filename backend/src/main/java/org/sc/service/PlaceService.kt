package org.sc.service

import org.sc.common.rest.PlaceDto
import org.sc.manager.PlaceManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceService @Autowired constructor(private val placeManager: PlaceManager) {


    fun fetchPaginated(skip: Int, limit: Int, realm: String, isDynamic: Boolean?): List<PlaceDto> {
        val isDynamicShowing = isDynamic != null && isDynamic
        return placeManager.getPaginated(skip, limit, realm, isDynamicShowing)
    }


    fun countByRealm(realm: String, isDynamic: Boolean): Long =
            placeManager.countByRealm(realm, isDynamic)


}