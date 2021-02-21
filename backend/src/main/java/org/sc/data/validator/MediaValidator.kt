package org.sc.data.validator

import org.sc.util.FileProbeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class MediaValidator @Autowired constructor(private val fileProbeUtil: FileProbeUtil) : Validator<File?> {
    companion object {
        val mimeAllowed = setOf("image/png", "image/jpeg")
        const val fileMimeError = "Posted file is not in a correct format"
    }

    override fun validate(request: File?): Set<String> {
        if (request == null) {
            return setOf("File is null")
        }
        val errors = mutableSetOf<String>()
        val fileMimeType = fileProbeUtil.getFileMimeType(request)

        if(fileMimeType !in mimeAllowed) {
            errors.add(fileMimeError)
        }
        return errors
    }
}