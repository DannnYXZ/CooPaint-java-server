package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.domain.Message;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChatReadHistoryCommand implements Command {
    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UUID chatUUID = UUID.fromString(props.get(0));
        try {
            var chatService = ServiceFactory.INSTANCE.getChatService();
            Chat chat = chatService.readRoom(chatUUID);
            var mapper = new ObjectMapper();
            ObjectNode jbody = mapper.createObjectNode();
            ArrayNode arr = mapper.createArrayNode();
            for (Message msg : chat.getElements()) {
                JsonNode jsonMessage = mapper.valueToTree(msg);
                arr.add(jsonMessage);
            }
            jbody.put("action", "add-messages");
            jbody.set("messages", arr);
            return (WSCommandResult) new WSCommandResult()
                    .setReceivers(Set.of((Session) session))
                    .setBody(mapper.writeValueAsString(jbody));
        } catch (ServiceException | JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
