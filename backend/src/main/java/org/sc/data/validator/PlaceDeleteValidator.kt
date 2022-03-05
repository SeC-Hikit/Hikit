package org.sc.data.validator

import org.sc.manager.TrailManager
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceDeleteValidator @Autowired constructor(
        private val placeExistenceValidator: PlaceExistenceValidator,
        private val trailManager: TrailManager) : Validator<String> {

    companion object {
        const val connectedTrails = "Some trails are still connected to the to be deleted place"
    }

    override fun validate(request: String): Set<String> {
        val listOfErrorMessages = mutableSetOf<String>()
        listOfErrorMessages.addAll(placeExistenceValidator.validatePlace(request))
        val byPlaceRefId = trailManager.getByPlaceRefId(request, 0, 1, TrailSimplifierLevel.LOW)
        if(byPlaceRefId.isNotEmpty()) listOfErrorMessages.add(connectedTrails)
        return listOfErrorMessages
    }

}