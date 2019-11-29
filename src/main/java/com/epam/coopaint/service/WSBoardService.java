package com.epam.coopaint.service;

import com.epam.coopaint.dao.BoardDAO;
import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.Pair;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.*;

@ApplicationScoped // access is sequential (transactions not supported)
public class WSBoardService {
    private static Logger logger = LogManager.getLogger();
    private final Map<UUID, Board> boards = new HashMap<>(); // <chat, messages> ~ persistent
    private final Map<UUID, Set<Session>> sessions = new HashMap<>(); // <chat, connections> ~ dynamic

    private Board createEmptyBoard() {
        //return new Board().setName("Unnamed Board").setUuid(UUID.randomUUID());
        return null;
    }

    // base method - must be called first
    public Pair<UUID, Set<Session>> connectTo(User user, String boardUUID, Session session) throws ServiceException {
        UUID uuid;
        try {
            uuid = UUID.fromString(boardUUID);
        } catch (IllegalArgumentException e) {
            uuid = UUID.randomUUID();
        }
        // check cache
        if (!boards.containsKey(uuid)) {
            // check db
            BoardDAO boardDAO = DAOFactory.INSTANCE.createBoardDAO();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) boardDAO);
                //Board board = boardDAO.readBoard(uuid);
                Board board = null;
                transaction.commit();
                boards.put(uuid, board); // put to cache
            } catch (DAOException e) {
                // no board -> create
                try {
                    //Board newBoard = createEmptyBoard().setCreator(user);
                    Board newBoard = null;
                    if (user.isAuth()) {
                        // storing only registered users
                        //boardDAO.createBoard(newBoard);
                        transaction.commit();
                    }
                    boards.put(uuid, newBoard); // put to cache
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

    public Board readBoard(UUID boardUUID) throws ServiceException {
        // check only cache (coz connectTo lifted board to cache)
        Board board = boards.get(boardUUID);
        if (board != null) {
            return board;
        } else {
            throw new ServiceException("No such board: " + boardUUID);
        }
    }

    public void saveBoard(UUID boardUUID) throws ServiceException { // FIXME: private
        // saving from virtual board
        Board board = boards.get(boardUUID);
        if (board != null && board.getCreator().isAuth()) {
            BoardDAO boardDAO = DAOFactory.INSTANCE.createBoardDAO();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) boardDAO);
                // saving board == update
                //boardDAO.updateBoard(board);
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

    public Pair<List<VShape>, Set<Session>> addShapes(UUID boardUUID, List<VShape> elements) {
        boards.get(boardUUID).getElements().addAll(elements);
        Set<Session> sessions = this.sessions.get(boardUUID);
        return new Pair<>(elements, sessions);
    }

    public List<Board> getUserBoardsMeta(UUID userUUID) throws ServiceException {
        BoardDAO boardDAO = DAOFactory.INSTANCE.createBoardDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) boardDAO);
  //          return boardDAO.readUserBoardsMeta(userUUID);
            return null;
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to get user boards.", e);
        } finally {
            transaction.end();
        }
    }
}
