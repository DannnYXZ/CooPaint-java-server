package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.domain.WSCommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.service.WSBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BoardAcceptElementCommand2 implements Command2 {
    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var mapper = new ObjectMapper();
            var boardService = CDI.current().select(WSBoardService.class).get();
            List<VShape> messages = Arrays.asList(mapper.readValue(body, VShape[].class));
            UUID chatUUID = UUID.fromString(props.get(0));
            Pair<List<VShape>, Set<Session>> processedMessages = boardService.addShapes(chatUUID, messages); // calc time and from
            ObjectNode jbody = mapper.createObjectNode();
            jbody.put("action", "add-elements");
            jbody.set("elements", mapper.valueToTree(processedMessages.getElement0()));
            WSCommandResult result = (WSCommandResult) new WSCommandResult().setBody(mapper.writeValueAsString(jbody));
            return result.setReceivers(processedMessages.getElement1());
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
