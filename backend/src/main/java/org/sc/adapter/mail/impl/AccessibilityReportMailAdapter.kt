package org.sc.adapter.mail.impl

import org.sc.adapter.MailAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class AccessibilityReportMailAdapter @Autowired constructor(private val mailAdapter: MailAdapter) {

    companion object {
        const val subject = "[S&C] Attivazione segnalazione"
        val template = String.format(MailAdapter.HTML_TEMPLATE, "Il %s, hai inviato una segnalazione " +
                "tramite S&C al '%s' per il sentiero '%s'. </br> Descrizione: '%s'.<br/>Per attivare la segnalazione, clicca <a href='%s'>qui</a>, " +
                "oppure copia incolla questo indirizzo nella barra indirizzi del browser: %s")
    }

    fun sendValidation(reportDate: Date, trailId: String,
                       realm: String, description: String,
                       activationId: List<String>, targetEmailAddress: String) {
        val body = String.format(template, mailAdapter.dateFormatter.format(reportDate),
                realm, trailId, description,
                mailAdapter.activationAddress + activationId)
        mailAdapter.send(subject, body, targetEmailAddress)
    }
}