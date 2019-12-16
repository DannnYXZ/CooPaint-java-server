package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.Pair;
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

// board metadata only
public class BoardUpdateCommand implements Command {
    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var mapper = new ObjectMapper();
            UUID boardUUID = UUID.fromString(props.get(0));
            Board updater = mapper.readValue(body, Board.class);
            updater.setUuid(boardUUID);
            var boardService = ServiceFactory.INSTANCE.getBoardService();
            Pair<Board, Set<Session>> pair = boardService.update(updater);
            ObjectNode jbody = mapper.createObjectNode();
            jbody.put("action", "update-board");
            jbody.set("board", mapper.valueToTree(pair.getElement0()));
            WSCommandResult result = (WSCommandResult) new WSCommandResult().setBody(mapper.writeValueAsString(jbody));
            return result.setReceivers(pair.getElement1());

        } catch (JsonProcessingException | ServiceException e) {
            throw new CommandException(e);
        }
    }
}
