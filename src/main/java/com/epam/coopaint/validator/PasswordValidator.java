package com.epam.coopaint.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum PasswordValidator {
    INSTANCE;
    private String pattern = null;

    // TODO: decorator
    public PasswordValidator buildValidator(boolean forceSpecialChar,
                                            boolean forceCapitalLetter,
                                            boolean forceNumber,
                                            int minLength,
                                            int maxLength) {
        StringBuilder patternBuilder = new StringBuilder("((?=.*[a-z])");

        if (forceSpecialChar) {
            patternBuilder.append("(?=.*[@#$%])");
        }
        if (forceCapitalLetter) {
            patternBuilder.append("(?=.*[A-Z])");
        }
        if (forceNumber) {
            patternBuilder.append("(?=.*d)");
        }
        patternBuilder.append(".{" + minLength + "," + maxLength + "})");
        pattern = patternBuilder.toString();

        return INSTANCE;
    }

    public boolean validatePassword(final String password) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
