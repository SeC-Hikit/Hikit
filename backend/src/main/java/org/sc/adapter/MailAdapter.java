package org.sc.adapter;

import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;

@Service
public class MailAdapter {

    public final static String HTML_TEMPLATE = "<html><head> <style> body { font-family: sans-serif; } .head { background: #1C9466;" +
            " width: 100%; min-height: 100px; } footer { text-align: center; padding: 3px; background-color: #1C9466; color: white; } " +
            "a:link { color: white; background-color: transparent; text-decoration: none; } a:visited { color: white; background-color: transparent; text-decoration: none; } a:hover { color: white; background-color: transparent; text-decoration: underline; } a:active { color: white; background-color: transparent; text-decoration: underline; } .title { margin-top: 40px; color: white; } </style></head><body><br><div class=\"head\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\"> <tr> <td> <h1 class=\"title\">Hikit</h1> </td> </tr> </table></div><br><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"90%\" align=\"center\"> <tr> <td> <p>";
    public final static String HTML_TEMPLATE_FOOTER =
            "</p> </td> </tr></table><br><footer> <p>Incontrato problemi? Scrivi a: <a href=\"mailto:support@sentieriecartografia.it\">support@sentieriecartografia.it</a></p> <p><a href=\"https://www.sentieriecartografia.it/\" target=\"_blank\">" +
            "Vai al sito del progetto</a></p></footer></body></html>";
    public final static String DATE_FORMAT = "dd-MM-yyyy";

    private final AppProperties appProperties;
    private final JavaMailSender javaMailSender;

    private final SimpleDateFormat dateFormatter;

    @Autowired
    public MailAdapter(final AppProperties appProperties,
                       final JavaMailSender javaMailSender) {
        this.appProperties = appProperties;
        this.javaMailSender = javaMailSender;
        this.dateFormatter = new SimpleDateFormat(DATE_FORMAT);
    }

    public void send(final String subject,
                     final String content,
                     final String... to) throws MessagingException {
        final MimeMessage msg = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(to);
        helper.setFrom(appProperties.getMailFrom());
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(msg);
    }

    public SimpleDateFormat getDateFormatter() {
        return dateFormatter;
    }

    public String getActivationAddress() {
        return appProperties.getValidationAddress();
    }
}
