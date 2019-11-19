package com.epam.coopaint.service;

import com.epam.coopaint.domain.Message;
import com.epam.coopaint.exception.ServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

@ApplicationScoped // access is sequential (transactions not supported)
public class WSChatService {
    private static Logger logger = LogManager.getLogger();
    private final Map<UUID, List<Message>> chatBoxes = new HashMap<>(); // <chat, messages>
    private int messageId = 0;

    public void addChatRoom(UUID chatUUID) {
        List<Message> messages = chatBoxes.computeIfAbsent(chatUUID, s -> new ArrayList<>());
        //chatSessions.add(session);
    }

    public UUID connectTo(String chatUUID) {
        UUID uuid;
        try {
            uuid = UUID.fromString(chatUUID);
        } catch (IllegalArgumentException e) {
            uuid = UUID.randomUUID();
        }
        chatBoxes.put(uuid, new ArrayList<>());
        return uuid;
    }

    public List<Message> readChatHistory(UUID chatUUID) throws ServiceException {
        return chatBoxes.get(chatUUID);
    }

    private JsonNode createPostMessages(Message message) {
        var mapper = new ObjectMapper();
        ObjectNode action = mapper.createObjectNode();
        action.put("action", "add-messages");
        action.set("message", mapper.valueToTree(message));
        return action;
    }

    public List<Message> addMessages(UUID chatBox, List<Message> messages) {
        chatBoxes.get(chatBox).addAll(messages);
        return messages;
    }

    private void sendToSession(Session session, JsonNode message) {
        var mapper = new ObjectMapper();
        try {
            session.getBasicRemote().sendText(mapper.writeValueAsString(message));
        } catch (IOException e) {
            chatBoxes.remove(session);
            logger.error(e);
        }
    }

    public void sendToAllConnectedSessions(String chatUUID, JsonNode message) {
        //for (Session session : sessions) {
        //sendToSession(session, message);
        //}
    }
}
