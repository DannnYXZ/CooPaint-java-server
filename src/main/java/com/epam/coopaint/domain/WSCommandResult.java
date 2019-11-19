package com.epam.coopaint.domain;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;

public class WSCommandResult extends CommandResult {
    private Set<Session> receivers = new HashSet<>();

    public Set<Session> getReceivers() {
        return receivers;
    }

    public WSCommandResult setReceivers(Set<Session> receivers) {
        this.receivers = receivers;
        return this;
    }
}
