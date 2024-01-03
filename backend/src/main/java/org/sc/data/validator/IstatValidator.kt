package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

@Component
class IstatValidator @Autowired constructor() : Validator<String> {
    companion object {
        const val noParamSpecifiedError = "Empty istat not accepted"
        const val istatNotCorrect = "Istat not in correct format"
        val istatRegex: Pattern = compile("^\\d{0,5}\$")
    }

    override fun validate(request: String): Set<String> {
        if (isEmpty(request)) {
            return setOf(noParamSpecifiedError)
        }

        if(!istatRegex.matcher(request).matches()) {
            return setOf(istatNotCorrect)
        }

        return setOf()
    }
}