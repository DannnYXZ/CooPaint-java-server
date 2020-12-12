package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.RoomDAO;
import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.Room;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SecurityService;
import com.epam.coopaint.service.WSService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.*;
import java.util.function.Supplier;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_HTTP;
import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;
import static com.epam.coopaint.domain.ACLData.GROUP_ALL;
import static com.epam.coopaint.domain.ResourceAction.*;

class WSServiceImpl<R extends Room<E>, E> implements WSService<R, E> {
    private static Logger logger = LogManager.getLogger();
    private final Map<UUID, R> rooms = new HashMap<>(); // <chat, messages> ~ persistent
    private final Map<UUID, Set<Session>> sessions = new HashMap<>(); // <chat, connections> ~ dynamic
    SecurityService securityService = ServiceFactory.INSTANCE.getSecurityService();
    Supplier<R> roomSupplier;
    Supplier<RoomDAO<R>> roomDaoSupplier;

    // base method - must be called first
    @Override
    public Pair<UUID, Set<Session>> connectTo(User authorizedUser, String roomUUID, Session session) throws ServiceException {
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
            SecurityDAO securityDAO = DAOFactory.INSTANCE.createSecurityDAO();
            UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) roomDAO, (GenericDAO) userDAO, (GenericDAO) securityDAO);
                R room = roomDAO.readRoom(uuid);
                User creator = userDAO.getUser(room.getCreator().getUuid());
                room.setCreator(creator);
                // get user
                transaction.commit();
                this.rooms.put(uuid, room); // put to cache
            } catch (DAOException e) {
                // no board -> create
                try {
                    R newRoom = roomSupplier.get();
                    newRoom.setCreator(authorizedUser).setUuid(UUID.randomUUID()).setElements(new ArrayList<>());
                    uuid = newRoom.getUuid();
                    if (authorizedUser.isAuth()) {
                        // storing only registered users
                        roomDAO.createRoom(newRoom);
                        securityDAO.createACL(uuid.toString(), authorizedUser.getUuid().toString(),
                                new HashSet<>(Arrays.asList(values())));
                        securityDAO.createACL(uuid.toString(), GROUP_ALL,
                                Set.of(READ_BOARD, UPDATE_BOARD, READ_CHAT, UPDATE_CHAT));
                        transaction.commit();
                    }
                    rooms.put(uuid, newRoom); // put to cache
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
        disconnectUsersWhoCannotReadRoom(uuid);
        return new Pair<>(uuid, sessions);
    }

    @Override
    public R readRoom(UUID roomUUID) throws ServiceException {
        // check only cache (coz connectTo lifted board to cache)
        R room = this.rooms.get(roomUUID);
        if (room != null) {
            return room;
        } else {
            throw new ServiceException("No such room: " + roomUUID);
        }
    }

    @Override
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

    @Override
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

    @Override
    public void saveRoom(UUID roomUUID) throws ServiceException {
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

    @Override
    public Pair<List<E>, Set<Session>> addElements(UUID roomUUID, List<E> elements) {
        rooms.get(roomUUID).getElements().addAll(elements);
        Set<Session> sessions = this.sessions.get(roomUUID);
        disconnectUsersWhoCannotReadRoom(roomUUID);
        return new Pair<>(elements, sessions);
    }

    @Override
    public Pair<R, Set<Session>> update(R updater) throws ServiceException {
        // virtual room first
        R room = rooms.get(updater.getUuid());
        Set<Session> receivers = new HashSet<>();
        if (room != null) {
            if (updater.getName() != null) room.setName(updater.getName());
            receivers = sessions.get(updater.getUuid());
        }
        // now db: 1 - not loaded, 2 - guest room
        if (room == null || room.getCreator().isAuth()) {
            var transaction = new TransactionManager();
            RoomDAO<R> roomDAO = roomDaoSupplier.get();
            try {
                transaction.begin((GenericDAO) roomDAO);
                roomDAO.updateRoom(updater);
                transaction.commit();
            } catch (DAOException e) {
                transaction.rollback();
                throw new ServiceException("Failed to update room: " + updater.getUuid(), e);
            } finally {
                transaction.end();
            }
        }
        disconnectUsersWhoCannotReadRoom(room.getUuid());
        return new Pair<>(room, receivers);
    }

    @Override
    public List<R> getUserRoomsMeta(UUID userUUID) throws ServiceException {
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

    public void disconnectUsersWhoCannotReadRoom(UUID roomUUID) {
        Set<Session> sessions = this.sessions.get(roomUUID);
        Set<Session> sessionsToRevoke = new HashSet<>();
        if (sessions != null) {
            sessions.forEach(session -> {
                HttpSession httpSession = (HttpSession) session.getUserProperties().get(SESSION_HTTP);
                User roomUser = (User) httpSession.getAttribute(SESSION_USER);
                try {
                    if (securityService.canAccess(roomUUID.toString(), READ_BOARD, roomUser) ||
                            securityService.canAccess(roomUUID.toString(), READ_CHAT, roomUser)) {
                        sessionsToRevoke.add(session);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            });
            sessions.retainAll(sessionsToRevoke);
        }
    }
}
