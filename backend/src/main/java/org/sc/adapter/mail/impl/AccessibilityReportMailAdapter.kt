package org.sc.adapter.mail.impl

import org.sc.adapter.MailAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Logger

@Component
class AccessibilityReportMailAdapter @Autowired constructor(private val mailAdapter: MailAdapter) {

    private val logger = Logger.getLogger(AccessibilityReportMailAdapter::class.java.name)

    companion object {
        const val subject = "[S&C] Attivazione segnalazione"
        val template = String.format(MailAdapter.HTML_TEMPLATE, ("Il %s, hai inviato una segnalazione " +
                "tramite S&C al '%s' per il sentiero '%s' (ID da sistema: '%s'). </br> Descrizione: '%s'.<br/>Per attivare la segnalazione, clicca <a href=\"%s\" target='_blank'>qui</a>, " +
                "oppure copia incolla questo indirizzo nella barra indirizzi del browser: %s"))
    }

    fun sendValidation(reportDate: Date, trailCode: String, trailId: String,
                       realm: String, description: String,
                       activationId: String, targetEmailAddress: String) {
        val activationLink = mailAdapter.activationAddress + activationId
        val body = String.format(
                template,
                mailAdapter.dateFormatter.format(reportDate),
                realm, trailCode, trailId, description,
                activationLink, activationLink
        )
        logger.info("Sending email to: $targetEmailAddress")
        mailAdapter.send(subject, body, targetEmailAddress)
    }
}