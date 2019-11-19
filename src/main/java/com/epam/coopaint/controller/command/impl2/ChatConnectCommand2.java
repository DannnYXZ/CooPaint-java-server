package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

public class ChatConnectCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        String chatUUID = props.get(0); // can be UUID or null
        UUID result = sessionHandler.connectTo(chatUUID);
        var mapper = new ObjectMapper();
        ObjectNode jbody = mapper.createObjectNode();
        jbody.put("action", "connect");
        jbody.put("id", result.toString());
        // define creator
        try {
            var out = new CommandResult().setBody(mapper.writeValueAsString(jbody));
            return out;
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
