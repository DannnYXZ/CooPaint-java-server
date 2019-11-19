package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.domain.Message;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

public class ChatReadHistoryCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UUID chatUUID = UUID.fromString(props.get(0));
        try {
            List<Message> messages = sessionHandler.readChatHistory(chatUUID);
            var mapper = new ObjectMapper();
            ObjectNode jbody = mapper.createObjectNode();
            ArrayNode arr = mapper.createArrayNode();
            for (Message msg : messages) {
                JsonNode jsonMessage = mapper.valueToTree(msg);
                arr.add(jsonMessage);
            }
            jbody.put("action", "add-messages");
            jbody.set("messages", arr);
            return new CommandResult().setBody(mapper.writeValueAsString(jbody));
        } catch (ServiceException | JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
