package org.sc.manager

import org.sc.common.rest.PlaceDto
import org.sc.data.repository.PlaceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceManager @Autowired constructor(placeDao: PlaceDAO) {


    fun getPaginated(page: Int, count: Int): List<PlaceDto> {
        TODO("Not yet implemented")
    }

    fun getLikeNameOrTags(name: String): List<PlaceDto> {
        TODO("Not yet implemented")
    }

    fun getById(id: String): List<PlaceDto> {
        TODO("Not yet implemented")
    }

    fun create(place: PlaceDto): List<PlaceDto> {
        TODO("Not yet implemented")
    }

    fun deleteById(id: Comparable<String>): List<PlaceDto> {
        TODO("Not yet implemented")
    }

    fun update(place: PlaceDto): List<PlaceDto> {
        TODO("Not yet implemented")
    }
}