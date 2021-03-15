package org.sc.data.validator

import org.sc.manager.PlaceManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceExistenceValidator @Autowired constructor(private val placeManager: PlaceManager) : Validator<String> {
    override fun validate(request: String): Set<String> {
        if (!placeManager.doesPlaceExist(request))
            mutableSetOf(String.format(ValidatorUtils.notExistingItem, "PLACE", request))
        return mutableSetOf()
    }
}