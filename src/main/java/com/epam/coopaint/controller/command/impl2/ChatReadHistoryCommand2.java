package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.Message;
import com.epam.coopaint.domain.WSCommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.WSChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChatReadHistoryCommand2 implements Command2 {
    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UUID chatUUID = UUID.fromString(props.get(0));
        try {
            var chatService = CDI.current().select(WSChatService.class).get();
            List<Message> messages = chatService.readChatHistory(chatUUID);
            var mapper = new ObjectMapper();
            ObjectNode jbody = mapper.createObjectNode();
            ArrayNode arr = mapper.createArrayNode();
            for (Message msg : messages) {
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
