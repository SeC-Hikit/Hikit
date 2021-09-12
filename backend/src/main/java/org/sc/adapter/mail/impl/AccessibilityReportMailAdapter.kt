package org.sc.adapter.mail.impl

import org.sc.adapter.MailAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityReportMailAdapter @Autowired constructor(private val mailAdapter: MailAdapter){
    fun sendValidation(reportDate: Date, trailId: String, realm: String) {
        // TODO
    }
}