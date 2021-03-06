package org.sc.data.validator

import org.apache.commons.lang3.StringUtils
import org.sc.util.FileProbeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path

@Component
class FileNameValidator @Autowired constructor(private val fileProbeUtil: FileProbeUtil) : Validator<String?> {
    companion object {
        const val fileNameError = "Posted filename is not in correct format"
    }

    override fun validate(request: String?): Set<String> {
        if (StringUtils.isEmpty(request)) {
            return setOf("Filename is empty")
        }
        val errors = mutableSetOf<String>()
        if(!fileProbeUtil.isFileNameInCorrectFormat(request!!)){
            errors.add(fileNameError)
        }
        return errors
    }
}