package com.epam.coopaint.validator;

import com.epam.coopaint.domain.SignInUpBundle;

public class UserValidator {
    private static final String REGEX_PASSWORD = ".*";
    private static final String REGEX_EMAIL = ".*";

    public static boolean isValid(SignInUpBundle bundle) {
        String email = bundle.getEmail();
        String password = bundle.getPassword();
        if (!(email != null && email.matches(REGEX_EMAIL))) {
            return false;
        }
        if (!(password != null && password.matches(REGEX_PASSWORD))) {
            return false;
        }
        return true;
    }
}
