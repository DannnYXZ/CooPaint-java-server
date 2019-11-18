package com.epam.coopaint.controller;

import com.epam.coopaint.service.WSChatService;
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
import java.util.UUID;

@ApplicationScoped
@ServerEndpoint(value = "/chat", configurator = CDIConfigurator.class)
public class WSChatController {
    private static Logger logger = LogManager.getLogger();

    @Inject
    private WSChatService chatService;

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        logger.info("New websocket session.");
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        session.getUserProperties().put(HttpSession.class.getName(), httpSession); // HTTP TTL < WS TTL
        UUID chatUUID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000"); // FIXME: take from ...
        chatService.addSession(chatUUID, session);
    }

    @OnClose
    public void close(Session session) {
        //chatService.removeSession(session);
        logger.info("Closed session.");
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("WebSocket error.", error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        HttpSession httpSession = (HttpSession) session.getUserProperties().get(HttpSession.class.getName());
        var mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(message);
            String url = rootNode.path("url").asText();
            String method = rootNode.path("method").asText();
            String body = mapper.writeValueAsString(rootNode.path("body"));
            CommandDispatcher.INSTANCE.dispatch(CommandDispatcher.Method.valueOf(method), url, body, httpSession);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
