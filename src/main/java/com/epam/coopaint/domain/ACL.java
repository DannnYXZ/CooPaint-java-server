package com.epam.coopaint.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ACL {
    private Map<String, Set<ResourceAction>> acl = new HashMap<>();

    public Map<String, Set<ResourceAction>> getAcl() {
        return acl;
    }

    public void addAction(String group, ResourceAction action) {
        Set<ResourceAction> actions = acl.computeIfAbsent(group, k -> new HashSet<>());
        actions.add(action);
    }

    public Set<ResourceAction> getActions(String group) {
        return acl.getOrDefault(group, new HashSet<>());
    }

    public Set<String> getGroups() {
        return new HashSet<>(acl.keySet());
    }
}
