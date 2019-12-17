package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.epam.coopaint.service.impl.WSBoardServiceImpl;
import com.epam.coopaint.service.impl.WSChatServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.epam.coopaint.command.impl.SessionAttribute.*;

public class SnapshotGetCommand implements Command {

    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        // returns if exists otherwise creates and returns
        String snapshotLink = !props.isEmpty() ? props.get(0) : "";
        var wsSession = (Session) session;
        var httpSession = (HttpSession) wsSession.getUserProperties().get(SESSION_HTTP);
        var user = (User) httpSession.getAttribute(SESSION_USER);
        var chatService = CDI.current().select(WSChatServiceImpl.class).get();
        var boardService = CDI.current().select(WSBoardServiceImpl.class).get();
        // check session
        Snapshot snap = (Snapshot) wsSession.getUserProperties().get(SESSION_SNAPSHOT);
        if (snap == null || !snap.getLink().equals(snapshotLink)) {
            // checking db
            SnapshotService snapshotService = ServiceFactory.INSTANCE.getSnapshotService();
            try {
                snap = snapshotService.readSnapshot(snapshotLink);
                chatService.connectTo(user, snap.getChat().getUuid().toString(), wsSession);
                boardService.connectTo(user, snap.getBoard().getUuid().toString(), wsSession);
            } catch (ServiceException e) {
                // no snapshot, allocating chat and board
                try {
                    Pair<UUID, Set<Session>> chat = chatService.connectTo(user, "", wsSession);
                    Pair<UUID, Set<Session>> board = boardService.connectTo(user, "", wsSession);
                    snap = snapshotService.createSnapshot(chat.getElement0(), board.getElement0(), user.isAuth());
                    wsSession.getUserProperties().put(SESSION_SNAPSHOT, snap);
                } catch (ServiceException ex) {
                    throw new CommandException("Failed to store snapshot.", ex);
                }
            }
        }
        try {
            // building response
            var mapper = new ObjectMapper();
            ObjectNode jbody = mapper.createObjectNode();
            jbody.set("body", mapper.valueToTree(snap));
            jbody.put("action", "add-snapshot");
            var result = (WSCommandResult) new WSCommandResult().setBody(mapper.writeValueAsString(jbody));
            return result.setReceivers(Set.of(wsSession));
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
