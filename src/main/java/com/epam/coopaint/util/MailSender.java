package com.epam.coopaint.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.epam.coopaint.util.MailData.EMAIL;
import static com.epam.coopaint.util.MailData.PASSWORD;

public class MailSender {
    private static Logger logger = LogManager.getLogger();
    private static MailSender instance = new MailSender();

    private MailSender() {
    }

    public static MailSender getInstance() {
        return instance;
    }

    public void sendMail(String mailContent, String mailTarget) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            };
            Session session = Session.getInstance(props, auth);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTarget));
            message.setSubject("CooPaint");
            message.setContent(mailContent, "text/html; charset=utf-8");
            // Transport.send(message);
            logger.info("Mail sending COMPLETE, target: " + mailTarget);
        } catch (MessagingException e) {
            logger.error("Mail sending FAILED, target: " + mailTarget);
        }
    }
}