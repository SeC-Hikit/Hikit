package org.sc.data.validator

import org.apache.tika.Tika
import org.sc.util.FileProbeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Component
class MediaValidator @Autowired constructor(private val fileProbeUtil: FileProbeUtil) : Validator<File> {
    companion object {

        val mimeAllowed = setOf("image/png", "image/jpeg")

        const val fileMimeError = "Posted file is not in a correct format"
    }

    override fun validate(request: File): Set<String> {
        val errors = mutableSetOf<String>()
        val fileMimeType = fileProbeUtil.getFileMimeType(request)
        if(fileMimeType !in mimeAllowed) {
            errors.add(fileMimeType)
        }
        return errors
    }
}