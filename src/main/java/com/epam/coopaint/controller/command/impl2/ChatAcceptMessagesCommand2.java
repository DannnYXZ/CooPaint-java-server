package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.Message;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ChatAcceptMessagesCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            // add to chat
            var mapper = new ObjectMapper();
            List<Message> messages = Arrays.asList(mapper.readValue(body, Message[].class));
            UUID chatUUID = UUID.fromString(props.get(0));
            List<Message> processedMessages = sessionHandler.addMessages(chatUUID, messages); // calc time and from
            ObjectNode jbody = mapper.createObjectNode();
            jbody.put("action", "add-messages");
            jbody.set("messages", mapper.valueToTree(processedMessages));
            return new CommandResult().setBody(mapper.writeValueAsString(jbody));
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
