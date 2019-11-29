package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.domain.WSCommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.service.WSBoardService2;
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
            var boardService = CDI.current().select(WSBoardService2.class).get();


            List<VShape> messages = Arrays.asList(mapper.readValue(body, VShape[].class));
            UUID boardUUID = UUID.fromString(props.get(0));
            Pair<List<VShape>, Set<Session>> processedElements = boardService.addElements(boardUUID, messages); // calc time and from
            ObjectNode jbody = mapper.createObjectNode();
            jbody.put("action", "add-elements");
            jbody.set("elements", mapper.valueToTree(processedElements.getElement0()));
            WSCommandResult result = (WSCommandResult) new WSCommandResult().setBody(mapper.writeValueAsString(jbody));
            return result.setReceivers(processedElements.getElement1());
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
