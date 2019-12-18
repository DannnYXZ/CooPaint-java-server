package com.epam.coopaint.service;

import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.Room;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;

import javax.websocket.Session;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * WSService - generalized room, such as chat room containing messages or board containing shapes
 * @param <E>  - type of elements of room
 * @param <R> - type of room that contains elements
 */
public interface WSService<R extends Room<E>, E> {
    Pair<UUID, Set<Session>> connectTo(User user, String roomUUID, Session session) throws ServiceException;
    R readRoom(UUID roomUUID) throws ServiceException;
    void saveRoom(UUID roomUUID) throws ServiceException;
    void deleteRoom(UUID roomUUID) throws ServiceException;
    void removeSession(Session session);
    /**
     * Adds elements to room
     * @param elements - elements to be added in room
     * @return - list of processed elements and set of receivers (sessions, connected to room)
     */
    Pair<List<E>, Set<Session>> addElements(UUID roomUUID, List<E> elements);
    Pair<R, Set<Session>> update(R updater) throws ServiceException;
    List<R> getUserRoomsMeta(UUID userUUID) throws ServiceException;
}
