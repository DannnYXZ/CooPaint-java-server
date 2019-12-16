package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BoardReadCommand implements Command {
    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UUID boardUUID = UUID.fromString(props.get(0));
        try {
            var boardService = ServiceFactory.INSTANCE.getBoardService();
            Board board = boardService.readRoom(boardUUID);
            var mapper = new ObjectMapper();
            ObjectNode jbody = mapper.createObjectNode();
            jbody.put("action", "read-board");
            jbody.set("board", mapper.valueToTree(board));
            return (WSCommandResult) new WSCommandResult()
                    .setReceivers(Set.of((Session) session))
                    .setBody(mapper.writeValueAsString(jbody));
        } catch (ServiceException | JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
