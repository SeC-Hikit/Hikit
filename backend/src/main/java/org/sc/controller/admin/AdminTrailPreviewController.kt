package org.sc.controller.admin

import io.swagger.v3.oas.annotations.Operation
import org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.service.TrailPreviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Constants.PREFIX_PREVIEW)
class AdminTrailPreviewController @Autowired constructor(
    private val trailPreviewService: TrailPreviewService,
    private val authRealmValidator: AuthRealmValidator
) {

    @Operation(summary = "Export a summary list of all saved trails for the target realm")
    @PostMapping("/list/export", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun downloadCsv(@RequestParam(required = true, defaultValue = NO_FILTERING_TOKEN) realm: String)
            : ResponseEntity<ByteArray> {
        if(!authRealmValidator.isAdminSameRealmAsResource(realm)) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body("Realm mismatch");
        }
        return ResponseEntity
            .ok(trailPreviewService.exportList(realm))
    }
}