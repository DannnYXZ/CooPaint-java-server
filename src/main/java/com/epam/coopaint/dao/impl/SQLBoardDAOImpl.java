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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.SQLData.*;

public class SQLBoardDAOImpl extends GenericDAO implements BoardDAO, RoomDAO<Board> {
    private static String QUERY_BOARD_CREATE = "INSERT INTO board  (board_uuid, board_name, board_data, board_creator) VALUES (?, COALESCE(?, board_name), ?, ?)";
    private static String QUERY_BOARD_READ = "SELECT * FROM board WHERE board_uuid=?";
    private static String QUERY_BOARD_UPDATE = "UPDATE board SET board_name=COALESCE(?, board_name)," +
            "board_data=COALESCE(?, board_data) WHERE board_uuid=?";
    private static String QUERY_BOARDS_BY_OWNER = "SELECT board.board_uuid, board.board_name FROM board JOIN user u ON board.board_creator = u.user_id AND u.user_uuid=?";
    private static String QUERY_BOARD_DELETE = "DELETE FROM board WHERE board_uuid=?";

    static RsToObject<Board> MAPPER_BOARD_ID = (s, b) -> b.setId(s.getLong(COLUMN_BOARD_ID));
    static RsToObject<Board> MAPPER_BOARD_UUID = (s, b) -> b.setUuid(Encryptor.bytesToUuid(s.getBytes(COLUMN_BOARD_UUID)));
    static RsToObject<Board> MAPPER_BOARD_NAME = (s, b) -> b.setName(s.getString(COLUMN_BOARD_NAME));
    static RsToObject<Board> MAPPER_BOARD_CREATOR = (s, b) -> b.setCreator(new User().setId(s.getLong(COLUMN_BOARD_CREATOR_ID)));
    static RsToObject<Board> MAPPER_BOARD_ELEMENTS = (s, b) -> b.getElements().addAll(Arrays.asList(new ObjectMapper()
            .readValue(s.getString(COLUMN_BOARD_DATA), VShape[].class)));

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
            selectStatement.setBytes(1, Encryptor.uuidToBytes(boardUUID));
            try (ResultSet result = selectStatement.executeQuery()) {
                RsToObjectListMapper<Board> mapper = new RsToObjectListMapper<>(List.of(
                        MAPPER_BOARD_ID,
                        MAPPER_BOARD_UUID,
                        MAPPER_BOARD_NAME,
                        MAPPER_BOARD_CREATOR,
                        MAPPER_BOARD_CREATOR,
                        MAPPER_BOARD_ELEMENTS));
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
    public Board updateRoom(Board room) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_BOARD_UPDATE)) {
            selectStatement.setString(1, room.getName());
            selectStatement.setString(2, new ObjectMapper().writeValueAsString(room.getElements()));
            selectStatement.setBytes(3, Encryptor.uuidToBytes(room.getUuid()));
            int n = selectStatement.executeUpdate();
            if (n == 1) {
                return room;
            }
            throw new DAOException("Failed to update board: " + room.getUuid());
        } catch (SQLException | JsonProcessingException e) {
            throw new DAOException("Failed to update board: " + room.getUuid(), e);
        }
    }

    @Override
    public void deleteRoom(UUID boardUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_BOARD_DELETE)) {
            selectStatement.setBytes(1, Encryptor.uuidToBytes(boardUUID));
            selectStatement.execute();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete board: " + boardUUID, e);
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
            selectStatement.setBytes(1, Encryptor.uuidToBytes(userUUID));
            try (ResultSet result = selectStatement.executeQuery()) {
                RsToObjectListMapper<Board> mapper = new RsToObjectListMapper<>(List.of(MAPPER_BOARD_UUID, MAPPER_BOARD_NAME));
                List<Board> boards = mapper.mapToList(result, Board::new);
                return boards;
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get board of: " + userUUID, e);
        }
    }
}
