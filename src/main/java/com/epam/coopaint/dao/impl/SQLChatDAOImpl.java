package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.util.Encryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.SQLData.*;

public class SQLChatDAOImpl extends GenericDAO implements RoomDAO<Chat> {
    private static String QUERY_CHAT_CREATE = "INSERT INTO chat (uuid, creator) VALUES (?,?)";
    private static String QUERY_CHAT_READ = "SELECT * FROM chat WHERE uuid=?";
    private static String QUERY_CHAT_UPDATE = "UPDATE chat SET name=COALESCE(?, name)," +
            "data=COALESCE(?, data) WHERE uuid=?";
    private static String QUERY_CHATS_BY_OWNER = "SELECT (uuid) FROM chat WHERE creator IN (SELECT (id) FROM user WHERE uuid=?)";
    private static String QUERY_CHAT_DELETE = "DELETE ";

    //
    @Override
    public Chat createRoom(Chat Chat) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_CHAT_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBytes(1, Encryptor.uuidToBytes(Chat.getUuid()));
            //statement.setString(2, new ObjectMapper().writeValueAsString(Chat.getElements()));
            statement.setLong(2, Chat.getCreator().getId());
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Chat.setId(generatedKeys.getLong(1));
                    return Chat;
                } else {
                    throw new DAOException("Failed to create Chat, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Chat readRoom(UUID ChatUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_CHAT_READ)) {
            selectStatement.setObject(1, ChatUUID, Types.BINARY);
            try (ResultSet result = selectStatement.executeQuery()) {
                RSMapper<Chat> mapper = new RSMapper<>(Arrays.asList(SQLChatDAOImpl.ChatMapper.values()));
                List<Chat> chats = mapper.mapToList(result, Chat::new);
                if (chats.isEmpty()) {
                    throw new DAOException("No Chat with uuid: " + ChatUUID);
                }
                return chats.get(0);
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get Chat by uuid: " + ChatUUID, e);
        }
    }

    @Override
    public Chat updateRoom(Chat Chat) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_CHAT_UPDATE)) {
            selectStatement.setString(1, new ObjectMapper().writeValueAsString(Chat.getElements()));
            selectStatement.setBytes(2, Encryptor.uuidToBytes(Chat.getUuid()));
            int n = selectStatement.executeUpdate();
            if (n == 1) {
                return Chat;
            }
            throw new DAOException("Failed to update Chat: " + Chat.getUuid());
        } catch (SQLException | JsonProcessingException e) {
            throw new DAOException("Failed to update Chat: " + Chat.getUuid(), e);
        }
    }

    @Override
    public List<Chat> readUserRoomsMeta(UUID userUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_CHATS_BY_OWNER)) {
            selectStatement.setObject(1, userUUID, Types.BINARY);
            try (ResultSet result = selectStatement.executeQuery()) {
                RSMapper<Chat> mapper = new RSMapper<>(List.of(SQLChatDAOImpl.ChatMapper.CHAT_UUID));
                List<Chat> Chats = mapper.mapToList(result, Chat::new);
                return Chats;
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get Chat of: " + userUUID, e);
        }
    }

    private enum ChatMapper implements RsToObjectMapper<Chat> {
        CHAT_ID((s, b) -> b.setId(s.getLong(COLUMN_CHAT_ID))),
        CHAT_UUID((s, b) -> b.setUuid(UUID.nameUUIDFromBytes(s.getBytes(COLUMN_CHAT_UUID))));
        public RsToObjectMapper func;

        ChatMapper(RsToObjectMapper<Chat> func) {
            this.func = func;
        }

        @Override
        public void apply(ResultSet rs, Chat board) throws DAOException {
            try {
                func.apply(rs, board);
            } catch (Exception e) {
                throw new DAOException("Failed to map.", e);
            }
        }
    }

}
