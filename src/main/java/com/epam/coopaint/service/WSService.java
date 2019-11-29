package com.epam.coopaint.service;

import com.epam.coopaint.dao.GenericDAO;
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
    private final Map<UUID, R> room = new HashMap<>(); // <chat, messages> ~ persistent
    private final Map<UUID, Set<Session>> sessions = new HashMap<>(); // <chat, connections> ~ dynamic
    Supplier<R> roomSupplier;
    Supplier<RoomDAO<R>> daoSupplier;

    // base method - must be called first
    public Pair<UUID, Set<Session>> connectTo(User user, String boardUUID, Session session) throws ServiceException {
        UUID uuid;
        try {
            uuid = UUID.fromString(boardUUID);
        } catch (IllegalArgumentException e) {
            uuid = UUID.randomUUID();
        }
        // check cache
        if (!room.containsKey(uuid)) {
            // check db
            RoomDAO<R> boardDAO = daoSupplier.get();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) boardDAO);
                R room = boardDAO.readRoom(uuid);
                transaction.commit();
                this.room.put(uuid, room); // put to cache
            } catch (DAOException e) {
                // no board -> create
                try {
                    R newBoard = roomSupplier.get();
                    newBoard.setCreator(user);
                    newBoard.setUuid(UUID.randomUUID());
                    if (user.isAuth()) {
                        // storing only registered users
                        boardDAO.createRoom(newBoard);
                        transaction.commit();
                    }
                    room.put(uuid, newBoard); // put to cache
                    uuid = newBoard.getUuid();
                } catch (DAOException ex) {
                    transaction.rollback();
                    throw new ServiceException("Failed to create new board.", ex);
                }
            } finally {
                transaction.end();
            }
        }
        Set<Session> sessions = this.sessions.computeIfAbsent(uuid, x -> new HashSet<>());
        sessions.add(session);
        return new Pair<>(uuid, sessions);
    }

    public R readRoom(UUID boardUUID) throws ServiceException {
        // check only cache (coz connectTo lifted board to cache)
        R board = room.get(boardUUID);
        if (board != null) {
            return board;
        } else {
            throw new ServiceException("No such board: " + boardUUID);
        }
    }

    public void saveBoard(UUID boardUUID) throws ServiceException { // FIXME: private
        // saving from virtual board
        R board = room.get(boardUUID);
        if (board != null && board.getCreator().isAuth()) {
            RoomDAO<R> boardDAO = daoSupplier.get();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) boardDAO);
                // saving board == update
                boardDAO.updateRoom(board);
                transaction.commit();

            } catch (DAOException e) {
                transaction.rollback();
                throw new ServiceException("Failed to save board", e);
            } finally {
                transaction.end();
            }
        }
    }

    public void removeSession(Session session) {
        for (Map.Entry<UUID, Set<Session>> elem : sessions.entrySet()) {
            Set<Session> sessions = elem.getValue();
            if (sessions.remove(session)) {
                try {
                    saveBoard(elem.getKey());
                } catch (ServiceException e) {
                    logger.error("Failed to save board: " + elem.getKey());
                }
            }
        }
    }

    public Pair<List<E>, Set<Session>> addElements(UUID boardUUID, List<E> elements) {
        room.get(boardUUID).getElements().addAll(elements);
        Set<Session> sessions = this.sessions.get(boardUUID);
        return new Pair<>(elements, sessions);
    }

    public List<R> getUserBoardsMeta(UUID userUUID) throws ServiceException {
        RoomDAO<R> boardDAO = daoSupplier.get();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) boardDAO);
            return boardDAO.readUserRoomsMeta(userUUID);
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to get user boards.", e);
        } finally {
            transaction.end();
        }
    }
}
