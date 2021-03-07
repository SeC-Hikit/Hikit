package org.sc.data.validator.trail

import org.sc.data.validator.Validator
import org.sc.data.validator.ValidatorUtils
import org.sc.manager.TrailManager
import org.springframework.stereotype.Component

@Component
class TrailExistenceValidator constructor(private val trailManager: TrailManager) : Validator<String> {
    override fun validate(request: String): Set<String> {
        val byId = trailManager.doesTrailExist(request)
        if(!byId) {
            return mutableSetOf((String.format(ValidatorUtils.notExistingItem, "Trail", request)))
        }
        return mutableSetOf()
    }
}