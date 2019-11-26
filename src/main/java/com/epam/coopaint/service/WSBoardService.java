package com.epam.coopaint.service;

import com.epam.coopaint.domain.Message;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.*;

@ApplicationScoped // access is sequential (transactions not supported)
public class WSBoardService {
    private static Logger logger = LogManager.getLogger();
    private final Map<UUID, List<VShape>> chatBoxes = new HashMap<>(); // <chat, messages> ~ persistent
    private final Map<UUID, Set<Session>> chatSessions = new HashMap<>(); // <chat, connections> ~ dynamic

    public Pair<UUID, Set<Session>> connectTo(String chatUUID, Session session) {
        UUID uuid;
        try {
            uuid = UUID.fromString(chatUUID);
        } catch (IllegalArgumentException e) {
            uuid = UUID.randomUUID();
        }
        chatBoxes.computeIfAbsent(uuid, x -> new ArrayList<>());
        Set<Session> sessions = chatSessions.computeIfAbsent(uuid, x -> new HashSet<>());
        sessions.add(session);
        return new Pair<>(uuid, sessions);
    }

    public List<VShape> readBoardHistory(UUID chatUUID) throws ServiceException {
        return chatBoxes.getOrDefault(chatUUID, new ArrayList<>());
    }

    public void removeSession(Session session) {
        for (Map.Entry<UUID, Set<Session>> elem : chatSessions.entrySet()) {
            Set<Session> sessions = elem.getValue();
            sessions.remove(session);
        }
    }

    public Pair<List<VShape>, Set<Session>> addShapes(UUID chatUUID, List<VShape> messages) {
        chatBoxes.get(chatUUID).addAll(messages);
        Set<Session> sessions = chatSessions.get(chatUUID);
        return new Pair<>(messages, sessions);
    }
}
