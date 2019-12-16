package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.RoomDAO;
import com.epam.coopaint.dao.RsToObject;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.util.Encryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.SQLColumns.*;

public class SQLChatDAOImpl extends GenericDAO implements RoomDAO<Chat> {
    private static String QUERY_CHAT_CREATE = "INSERT INTO chat (chat_uuid, chat_creator) VALUES (?,?)";
    private static String QUERY_CHAT_READ = "SELECT * FROM chat JOIN user u ON u.user_id = chat_creator WHERE chat_uuid=?";
    private static String QUERY_CHAT_UPDATE = "UPDATE chat SET chat_name=COALESCE(?, chat_name) WHERE chat_uuid=?";
    private static String QUERY_CHATS_BY_OWNER = "SELECT (chat_uuid) FROM chat WHERE chat_creator IN (SELECT (chat_id) FROM user WHERE chat_uuid=?)";

    static RsToObject<Chat> MAPPER_CHAT_ID = (s, c) -> c.setId(s.getLong(COLUMN_CHAT_ID));
    static RsToObject<Chat> MAPPER_CHAT_UUID = (s, c) -> c.setUuid(Encryptor.bytesToUuid(s.getBytes(COLUMN_CHAT_UUID)));
    static RsToObject<Chat> MAPPER_CHAT_CREATOR_UUID = (s, c) -> c.setCreator(new User().setUuid(Encryptor.bytesToUuid(s.getBytes(COLUMN_USER_UUID))));

    @Override
    public Chat createRoom(Chat Chat) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_CHAT_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBytes(1, Encryptor.uuidToBytes(Chat.getUuid()));
            statement.setLong(2, Chat.getCreator().getId());
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Chat.setId(generatedKeys.getLong(1));
                    Chat.setElements(new ArrayList<>());
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
    public Chat readRoom(UUID chatUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_CHAT_READ)) {
            selectStatement.setBytes(1, Encryptor.uuidToBytes(chatUUID));
            try (ResultSet result = selectStatement.executeQuery()) {
                RsToObjectListMapper<Chat> mapper = new RsToObjectListMapper<>(Arrays.asList(
                        MAPPER_CHAT_ID,
                        MAPPER_CHAT_UUID,
                        MAPPER_CHAT_CREATOR_UUID
                ));
                List<Chat> chats = mapper.mapToList(result, Chat::new);
                if (chats.isEmpty()) {
                    throw new DAOException("No chat with uuid: " + chatUUID);
                }
                return chats.get(0);
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get chat by uuid: " + chatUUID, e);
        }
    }

    @Override
    public Chat updateRoom(Chat room) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_CHAT_UPDATE)) {
            selectStatement.setString(1, new ObjectMapper().writeValueAsString(room.getElements()));
            selectStatement.setBytes(2, Encryptor.uuidToBytes(room.getUuid()));
            int n = selectStatement.executeUpdate();
            if (n == 1) {
                return room;
            }
            throw new DAOException("Failed to update Chat: " + room.getUuid());
        } catch (SQLException | JsonProcessingException e) {
            throw new DAOException("Failed to update Chat: " + room.getUuid(), e);
        }
    }

    @Override
    public void deleteRoom(UUID roomUUID) {
        // done by db
    }

    @Override
    public List<Chat> readUserRoomsMeta(UUID userUUID) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_CHATS_BY_OWNER)) {
            selectStatement.setObject(1, userUUID, Types.BINARY);
            try (ResultSet result = selectStatement.executeQuery()) {
                RsToObjectListMapper<Chat> mapper = new RsToObjectListMapper<>(List.of(MAPPER_CHAT_UUID));
                List<Chat> Chats = mapper.mapToList(result, Chat::new);
                return Chats;
            }
        } catch (Exception e) {
            throw new DAOException("Failed to get Chat of: " + userUUID, e);
        }
    }
}
