package com.epam.coopaint.domain;

import java.util.Set;

public class UserResourceActions {
  private User user;
  private Set<ResourceAction> actions;

    public UserResourceActions(User user, Set<ResourceAction> actions) {
        this.user = user;
        this.actions = actions;
    }

    public User getUser() {
        return user;
    }

    public UserResourceActions setUser(User user) {
        this.user = user;
        return this;
    }

    public Set<ResourceAction> getActions() {
        return actions;
    }

    public UserResourceActions setActions(Set<ResourceAction> actions) {
        this.actions = actions;
        return this;
    }
}
