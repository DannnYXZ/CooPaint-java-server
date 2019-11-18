package com.epam.coopaint.domain;

public class SignInUpBundle {
    private String email;
    private String password;

    public SignInUpBundle() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
