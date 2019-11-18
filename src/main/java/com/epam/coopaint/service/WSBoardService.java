package com.epam.coopaint.service;

import com.epam.coopaint.domain.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class WSBoardService {
    private static Logger logger = LogManager.getLogger();
    private static ObjectMapper mapper = new ObjectMapper();
    private int messageId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public void addSession(Session session) {
        sessions.add(session);
        for (Message message : messages) {
            JsonNode addMessage = createAddMessage(message);
            sendToSession(session, addMessage);
        }
    }

    private JsonNode createAddMessage(Message message) {
        ObjectNode action = mapper.createObjectNode();
        action.put("action", "put-msg");
        action.put("message", mapper.valueToTree(message));
        return action;
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(JsonNode msg) {
        try {
            Message message = mapper.treeToValue(msg, Message.class);
            message.setId(messageId++);
            messages.add(message);
            sendToAllConnectedSessions(createAddMessage(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void removeMessage(int id) {
        Iterator<Message> it = messages.iterator();
        while (it.hasNext()) {
            Message msg = it.next();
            if (msg.getId() == id) {
                it.remove();
                return;
            }
        }
    }

    public void toggleDevice(int id) {
    }

    private Message getDeviceById(int id) {
        return null;
    }

    private void sendToSession(Session session, JsonNode message) {
        try {
            session.getBasicRemote().sendText(mapper.writeValueAsString(message));
        } catch (IOException e) {
            sessions.remove(session);
            logger.error(e);
        }
    }

    public void sendToAllConnectedSessions(JsonNode message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }
}
