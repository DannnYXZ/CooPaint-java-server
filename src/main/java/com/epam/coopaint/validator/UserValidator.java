package com.epam.coopaint.validator;

import com.epam.coopaint.domain.SignInUpBundle;

public enum UserValidator {
    INSTANCE;
    private static final int PASSWORD_MIN_LEN = 4;
    private static final int PASSWORD_MAX_LEN = 255;
    private static final PasswordValidator passwordValidator = new PasswordValidator(
            false,
            true,
            true,
            PASSWORD_MIN_LEN,
            PASSWORD_MAX_LEN);

    public boolean isValid(SignInUpBundle bundle) {
        String email = bundle.getEmail();
        String password = bundle.getPassword();
        if (email == null || !W3EmailValidator.INSTANCE.isValid(email)) {
            return false;
        }
        if (password == null || !passwordValidator.isValidPassword(password)) {
            return false;
        }
        return true;
    }
}
