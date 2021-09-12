package org.sc.adapter;

import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailAdapter {

    private final AppProperties appProperties;
    private final JavaMailSender javaMailSender;

    @Autowired
    public MailAdapter(final AppProperties appProperties,
                       final JavaMailSender javaMailSender) {
        this.appProperties = appProperties;
        this.javaMailSender = javaMailSender;
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

}
