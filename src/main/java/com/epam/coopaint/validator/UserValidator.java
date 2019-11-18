package com.epam.coopaint.validator;

import com.epam.coopaint.domain.SignInUpBundle;
import org.apache.commons.validator.routines.EmailValidator;

public enum UserValidator {
    INSTANCE;
    private static final int PASSWORD_MIN_LEN = 3;
    private static final int PASSWORD_MAX_LEN = 4096;

    public boolean isValid(SignInUpBundle bundle) {
        String email = bundle.getEmail();
        String password = bundle.getPassword();
        if (!(email != null && EmailValidator.getInstance().isValid(email))) {
            return false;
        }
        PasswordValidator passwordValidator = PasswordValidator.INSTANCE.buildValidator(true, true, true, PASSWORD_MIN_LEN, PASSWORD_MAX_LEN);
        if (!(password != null && passwordValidator.validatePassword(password))) {
            return false;
        }
        return true;
    }
}
