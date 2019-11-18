package com.epam.coopaint.service;

import com.epam.coopaint.domain.Message;
import com.epam.coopaint.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
    private static ObjectMapper mapper = new ObjectMapper();
    private final Map<UUID, Set<Session>> sessions = new HashMap<>();
    private final List<Message> messages = new ArrayList<>();
    private int messageId = 0;

    public void addSession(UUID chatUUID, Session session) {
        Set<Session> chatSessions = sessions.computeIfAbsent(chatUUID, s -> new HashSet<>());
        chatSessions.add(session);
    }

    public void removeSession(UUID chatUUID, Session session) {
        sessions.get(chatUUID).remove(session);
        // TODO: notify (<username> left)
    }

    public String readChatHistory(UUID chatUUID) throws ServiceException {
        var mapper = new ObjectMapper();
        ObjectNode bundle = mapper.createObjectNode();
        ArrayNode arr = mapper.createArrayNode();
        for (Message msg : messages) {
            JsonNode jsonMessage = mapper.valueToTree(msg);
            arr.add(jsonMessage);
        }
        bundle.set("messages", arr);
        try {
            return mapper.writeValueAsString(bundle);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
    }

    private JsonNode createPostMessages(Message message) {
        ObjectNode action = mapper.createObjectNode();
        action.put("action", "add-messages");
        action.set("message", mapper.valueToTree(message));
        return action;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(JsonNode msg) {
        try {
            Message message = mapper.treeToValue(msg, Message.class);
            message.setId(messageId++);
            messages.add(message);
            //sendToAllConnectedSessions(createPostMessages(message));
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

    public void sendToAllConnectedSessions(String chatUUID, JsonNode message) {
        //for (Session session : sessions) {
            //sendToSession(session, message);
        //}
    }
}
