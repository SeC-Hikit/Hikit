package org.sc.data.validator.poi

import org.sc.data.validator.Validator
import org.sc.data.validator.ValidatorUtils.Companion.notExistingItem
import org.sc.manager.PoiManager
import org.springframework.stereotype.Component


@Component
class PoiExistenceValidator constructor(private val poiManager: PoiManager) : Validator<String> {
    override fun validate(request: String): Set<String> {
        val byId = poiManager.doesPoiExist(request)
        if (!byId) {
            return mutableSetOf(String.format(notExistingItem, "POI", request))
        }
        return mutableSetOf()
    }
}