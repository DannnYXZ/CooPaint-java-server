package com.epam.coopaint.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum W3EmailValidator {
    INSTANCE;
    private static final String w3Pattern = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    Pattern compiledPattern = Pattern.compile(w3Pattern);

    boolean isValid(String email) {
        Matcher m = compiledPattern.matcher(email);
        return m.matches();
    }
}
