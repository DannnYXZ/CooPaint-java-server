package com.epam.coopaint.domain;

import java.util.*;

public class ACL {
    private Map<String, Set<UserAction>> acl = new HashMap<>();

    public Map<String, Set<UserAction>> getAcl() {
        return acl;
    }

    public void addAction(String group, UserAction action) {
        Set<UserAction> actions = acl.computeIfAbsent(group, k -> new HashSet<>());
        actions.add(action);
    }

    public Set<UserAction> getActions(String group) {
        return acl.getOrDefault(group, new HashSet<>());
    }

    public Set<String> getGroups() {
        return new HashSet<>(acl.keySet());
    }
}
