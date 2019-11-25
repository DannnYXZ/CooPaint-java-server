package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.WSCommandResult;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChatConnectCommand2 extends WSCommand {
    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        String chatUUID = props.get(0); // can be UUID or null
        var wsSession = (Session) session;
        Pair<UUID, Set<Session>> result = chatService.connectTo(chatUUID, wsSession);
        var mapper = new ObjectMapper();
        ObjectNode jbody = mapper.createObjectNode();
        jbody.put("action", "connect");
        jbody.put("id", result.getElement0().toString());
        // TODO: define creator
        try {
            var out = (WSCommandResult) new WSCommandResult().setBody(mapper.writeValueAsString(jbody));
            return out;
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
