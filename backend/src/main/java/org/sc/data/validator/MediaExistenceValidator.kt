package org.sc.data.validator

import org.sc.manager.MediaManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MediaExistenceValidator @Autowired constructor(private val mediaManager: MediaManager) : Validator<String> {
    override fun validate(request: String): Set<String> {
        if (!mediaManager.doesMediaExist(request))
            mutableSetOf(String.format(ValidatorUtils.notExistingItem, "MEDIA", request))
        return mutableSetOf()
    }
}