package com.epam.coopaint.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PasswordValidator {
    private String pattern;

    PasswordValidator(boolean forceSpecialChar,
                             boolean forceCapitalLetter,
                             boolean forceNumber,
                             int minLength,
                             int maxLength) {
        StringBuilder patternBuilder = new StringBuilder("^(?=.*[a-z])");

        if (forceSpecialChar) {
            patternBuilder.append("(?=.*[~`!@#$%^&*()-_+={}[]|\\;:\"<>,./?])");
        }
        if (forceCapitalLetter) {
            patternBuilder.append("(?=.*[A-Z])");
        }
        if (forceNumber) {
            patternBuilder.append("(?=.*\\d)");
        }
        patternBuilder.append(".{").append(minLength).append(",").append(maxLength).append("}$");
        pattern = patternBuilder.toString();
    }

    boolean isValidPassword(String password) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
