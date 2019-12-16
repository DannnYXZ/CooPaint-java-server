package com.epam.coopaint.controller;

import com.epam.coopaint.command.impl.WSCommandResult;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.service.impl.WSBoardServiceImpl;
import com.epam.coopaint.service.impl.WSChatServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_HTTP;

@ApplicationScoped
@ServerEndpoint(value = "/ws", configurator = CDIConfigurator.class)
public class WSController {
    private static Logger logger = LogManager.getLogger();

    @Inject
    private WSChatServiceImpl chatService;
    @Inject
    private WSBoardServiceImpl boardService;

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        logger.info("New websocket session.");
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(SESSION_HTTP);
        session.getUserProperties().put(SESSION_HTTP, httpSession); // HTTP TTL < WS TTL
    }

    @OnClose
    public void close(Session session) {
        chatService.removeSession(session);
        boardService.removeSession(session);
        logger.info("Closed session.");
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("WebSocket error.", error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        HttpSession httpSession = (HttpSession) session.getUserProperties().get(SESSION_HTTP);
        var mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(message);
            String url = rootNode.path("url").asText();
            String method = rootNode.path("method").asText();
            String body = mapper.writeValueAsString(rootNode.path("body"));
            CommandResult result = CommandProvider.INSTANCE
                    .dispatch(CommandProvider.Method.valueOf(method), url, body, httpSession, session);
            if (result.getClass() == WSCommandResult.class) {
                sendMessage((WSCommandResult) result);
            } else {
                sendMessage(result.getBody(), session);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("Failed to send message.", e);
        }
    }

    private void sendMessage(WSCommandResult message) {
        for (Session session : message.getReceivers()) {
            sendMessage(message.getBody(), session);
        }
    }
}
