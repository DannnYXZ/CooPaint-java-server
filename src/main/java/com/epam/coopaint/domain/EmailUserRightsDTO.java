package com.epam.coopaint.domain;

import java.util.Set;

public class EmailUserRightsDTO {
    private String email;
    private Set<ResourceAction> actions;

    public String getEmail() {
        return email;
    }

    public EmailUserRightsDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Set<ResourceAction> getActions() {
        return actions;
    }

    public EmailUserRightsDTO setActions(Set<ResourceAction> actions) {
        this.actions = actions;
        return this;
    }
}
