package com.epam.coopaint.util;

import java.util.Properties;

class MailData {
    static final String EMAIL;
    static final String PASSWORD;

    static {
        try {
            Properties props = PropertyLoader.loadProperties(MailData.class.getResource("mail.properties").getPath());
            EMAIL = props.getProperty("email");
            PASSWORD = props.getProperty("password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize mailer data.");
        }
    }
}
