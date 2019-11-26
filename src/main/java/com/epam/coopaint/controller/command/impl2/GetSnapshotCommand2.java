package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.WSCommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.ServiceFactory;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.service.WSBoardService;
import com.epam.coopaint.service.WSChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_HTTP;
import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class GetSnapshotCommand2 implements Command2 {

    private static final String CHAT_UUID = "CHAT_UUID";
    private static final String BOARD_UUID = "BOARD_UUID";
    private static final String SNAPSHOT = "SNAPSHOT";

    @Override
    public WSCommandResult execute(List<String> props, String body, Object session) throws CommandException {
        // returns if exists otherwise creates and returns
        String snapshotLink = !props.isEmpty() ? props.get(0) : "";
        var wsSession = (Session) session;
        var httpSession = (HttpSession) wsSession.getUserProperties().get(SESSION_HTTP);
        var user = (User) httpSession.getAttribute(SESSION_USER);
        var chatService = CDI.current().select(WSChatService.class).get();
        var boardService = CDI.current().select(WSBoardService.class).get();

        Snapshot snap = (Snapshot) wsSession.getUserProperties().get(SNAPSHOT);
        if (snap == null) {
            SnapshotService snapshotService = ServiceFactory.getInstance().getSnapshotService();
            try {
                snap = snapshotService.getSnapshot(snapshotLink);
                chatService.connectTo(snap.getChatID().toString(), wsSession);
                boardService.connectTo(snap.getBoardID().toString(), wsSession);
            } catch (ServiceException e) {
                // no snapshot, allocating chat and board
                Pair<UUID, Set<Session>> chat = chatService.connectTo("", wsSession);
                Pair<UUID, Set<Session>> board = boardService.connectTo("", wsSession);
                // TODO: board
                try {
                    snap = snapshotService.createSnapshot(chat.getElement0(), board.getElement0(), false);
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
            WSCommandResult result = (WSCommandResult) new WSCommandResult().setBody(mapper.writeValueAsString(jbody));
            return result.setReceivers(Set.of(wsSession));
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
