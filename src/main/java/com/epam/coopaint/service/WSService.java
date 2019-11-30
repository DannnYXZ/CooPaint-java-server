package com.epam.coopaint.service;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.impl.RoomDAO;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.Room;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.Session;
import java.util.*;
import java.util.function.Supplier;

public class WSService<R extends Room<E>, E> {
    private static Logger logger = LogManager.getLogger();
    private final Map<UUID, R> rooms = new HashMap<>(); // <chat, messages> ~ persistent
    private final Map<UUID, Set<Session>> sessions = new HashMap<>(); // <chat, connections> ~ dynamic
    Supplier<R> roomSupplier;
    Supplier<RoomDAO<R>> roomDaoSupplier;

    // base method - must be called first
    public Pair<UUID, Set<Session>> connectTo(User user, String roomUUID, Session session) throws ServiceException {
        UUID uuid;
        try {
            uuid = UUID.fromString(roomUUID);
        } catch (IllegalArgumentException e) {
            uuid = UUID.randomUUID();
        }
        // check cache
        if (!rooms.containsKey(uuid)) {
            // check db
            RoomDAO<R> roomDAO = roomDaoSupplier.get();
            UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) roomDAO, (GenericDAO) userDAO);
                R room = roomDAO.readRoom(uuid);
                User creator = userDAO.getUser(room.getCreator().getId());
                room.setCreator(creator);
                // get user
                transaction.commit();
                this.rooms.put(uuid, room); // put to cache
            } catch (DAOException e) {
                // no board -> create
                try {
                    R newRoom = roomSupplier.get();
                    newRoom.setCreator(user);
                    newRoom.setUuid(UUID.randomUUID());
                    if (user.isAuth()) {
                        // storing only registered users
                        roomDAO.createRoom(newRoom);
                        transaction.commit();
                    }
                    rooms.put(uuid, newRoom); // put to cache
                    uuid = newRoom.getUuid();
                } catch (DAOException ex) {
                    transaction.rollback();
                    throw new ServiceException("Failed to create new room.", ex);
                }
            } finally {
                transaction.end();
            }
        }
        Set<Session> sessions = this.sessions.computeIfAbsent(uuid, x -> new HashSet<>());
        sessions.add(session);
        return new Pair<>(uuid, sessions);
    }

    public R readRoom(UUID roomUUID) throws ServiceException {
        // check only cache (coz connectTo lifted board to cache)
        R room = this.rooms.get(roomUUID);
        if (room != null) {
            return room;
        } else {
            throw new ServiceException("No such room: " + roomUUID);
        }
    }

    private void saveRoom(UUID roomUUID) throws ServiceException {
        // saving from virtual room
        R room = rooms.get(roomUUID);
        // if room was created by registered user, guest drawings will be saved too
        if (room != null && room.getCreator().isAuth()) {
            RoomDAO<R> roomDAO = roomDaoSupplier.get();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) roomDAO);
                // saving board == update
                roomDAO.updateRoom(room);
                transaction.commit();
            } catch (DAOException e) {
                transaction.rollback();
                throw new ServiceException("Failed to save board", e);
            } finally {
                transaction.end();
            }
        }
    }

    public void deleteRoom(UUID roomUUID) throws ServiceException {
        // check for cached room
        R room = rooms.get(roomUUID);
        // wipe from virtual space
        sessions.remove(roomUUID);
        rooms.remove(roomUUID);
        if (room != null && !room.getCreator().isAuth()) {
            // no need to access db if room is created by guest
            return;
        }
        var transaction = new TransactionManager();
        RoomDAO<R> roomDAO = roomDaoSupplier.get();
        try {
            transaction.begin((GenericDAO) roomDAO);
            roomDAO.deleteRoom(roomUUID);
            transaction.commit();
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to delete room: " + roomUUID, e);
        } finally {
            transaction.end();
        }
    }

    public void removeSession(Session session) {
        for (Map.Entry<UUID, Set<Session>> elem : sessions.entrySet()) {
            Set<Session> sessions = elem.getValue();
            if (sessions.remove(session)) {
                try {
                    saveRoom(elem.getKey());
                } catch (ServiceException e) {
                    logger.error("Failed to save board: " + elem.getKey());
                }
            }
        }
    }

    public Pair<List<E>, Set<Session>> addElements(UUID roomUUID, List<E> elements) {
        rooms.get(roomUUID).getElements().addAll(elements);
        Set<Session> sessions = this.sessions.get(roomUUID);
        return new Pair<>(elements, sessions);
    }

    public List<R> getUserBoardsMeta(UUID userUUID) throws ServiceException {
        RoomDAO<R> roomDAO = roomDaoSupplier.get();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) roomDAO);
            return roomDAO.readUserRoomsMeta(userUUID);
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to get user boards.", e);
        } finally {
            transaction.end();
        }
    }
}
