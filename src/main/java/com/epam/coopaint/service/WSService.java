package com.epam.coopaint.service;

import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.Room;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WSService<R extends Room<E>, E> {
    // base method - must be called first
    Pair<UUID, Set<Session>> connectTo(User user, String roomUUID, Session session) throws ServiceException;
    R readRoom(UUID roomUUID) throws ServiceException;
    void saveRoom(UUID roomUUID) throws ServiceException;
    void deleteRoom(UUID roomUUID) throws ServiceException;
    void removeSession(Session session);
    Pair<List<E>, Set<Session>> addElements(UUID roomUUID, List<E> elements);
    Pair<R, Set<Session>> update(R updater) throws ServiceException;
    List<R> getUserBoardsMeta(UUID userUUID) throws ServiceException;
}
