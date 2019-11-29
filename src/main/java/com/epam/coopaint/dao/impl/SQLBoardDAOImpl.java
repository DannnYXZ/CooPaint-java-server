package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.BoardDAO;
import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.util.Encryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.SQLData.*;

public class SQLBoardDAOImpl extends GenericDAO implements BoardDAO, RoomDAO<Board> {
    private static String QUERY_BOARD_CREATE = "INSERT INTO board  (uuid, name, data, creator) VALUES (?, ?, ?, ?)";
    private static String QUERY_BOARD_READ = "SELECT * FROM board WHERE uuid=?";
    private static String QUERY_BOARD_UPDATE = "UPDATE board SET name=COALESCE(?, name)," +
            "data=COALESCE(?, data) WHERE uuid=?";
    private static String QUERY_BOARDS_BY_OWNER = "SELECT (uuid, name) FROM board WHERE creator IN (SELECT (id) FROM user WHERE uuid=?)";
    private static String QUERY_BOARD_DELETE = "DELETE ";

    @Override
    public Board createRoom(Board board) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_BOARD_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBytes(1, Encryptor.uuidToBytes(board.getUuid()));
            statement.setString(2, board.getName());
            statement.setString(3, new ObjectMapper().writeValueAsString(board.getElements()));
            statement.setLong(4, board.getCreator().getId());
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    board.setId(generatedKeys.getLong(1));
                    return board;
                } else {
                    throw new DAOException("Failed to create board, no ID obtained.");
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Board readRoom(UUID boardUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_BOARD_READ)) {
            selectStatement.setObject(1, boardUUID, Types.BINARY);
            try (ResultSet result = selectStatement.executeQuery()) {
                RSMapper<Board> mapper = new RSMapper<>(Arrays.asList(BoardMapper.values()));
                List<Board> boards = mapper.mapToList(result, Board::new);
                if (boards.isEmpty()) {
                    throw new DAOException("No board with uuid: " + boardUUID);
                }
                return boards.get(0);
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get board by uuid: " + boardUUID, e);
        }
    }

    @Override
    public Board updateRoom(Board board) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_BOARD_UPDATE)) {
            selectStatement.setString(1, board.getName());
            selectStatement.setString(2, new ObjectMapper().writeValueAsString(board.getElements()));
            selectStatement.setBytes(3, Encryptor.uuidToBytes(board.getUuid()));
            int n = selectStatement.executeUpdate();
            if (n == 1) {
                return board;
            }
            throw new DAOException("Failed to update board: " + board.getUuid());
        } catch (SQLException | JsonProcessingException e) {
            throw new DAOException("Failed to update board: " + board.getUuid(), e);
        }
    }

    @Override
    public void getRights(User user, long accessRights) {

    }

    @Override
    public void setRights(User user, long accessRights) {

    }

    @Override
    public List<Board> readUserRoomsMeta(UUID userUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_BOARDS_BY_OWNER)) {
            selectStatement.setObject(1, userUUID, Types.BINARY);
            try (ResultSet result = selectStatement.executeQuery()) {
                RSMapper<Board> mapper = new RSMapper<>(Arrays.asList(BoardMapper.values()));
                List<Board> boards = mapper.mapToList(result, Board::new);
                return boards;
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get board of: " + userUUID, e);
        }
    }

    private enum BoardMapper implements RsToObjectMapper<Board> {
        BOARD_ID((s, b) -> b.setId(s.getLong(COLUMN_BOARD_ID))),
        BOARD_UUID((s, b) -> b.setUuid(UUID.nameUUIDFromBytes(s.getBytes(COLUMN_BOARD_UUID)))),
        BOARD_NAME((s, b) -> b.setName(s.getString(COLUMN_BOARD_NAME))),
        BOARD_ELEMENTS((s, b) -> b.getElements().addAll(Arrays.asList(new ObjectMapper()
                .readValue(s.getString(COLUMN_BOARD_DATA), VShape[].class))));
        public RsToObjectMapper func;

        BoardMapper(RsToObjectMapper<Board> func) {
            this.func = func;
        }

        @Override
        public void apply(ResultSet rs, Board board) throws DAOException {
            try {
                func.apply(rs, board);
            } catch (Exception e) {
                throw new DAOException("Failed to map.", e);
            }
        }
    }
}
