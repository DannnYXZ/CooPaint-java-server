package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.RsToObject;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.util.Encryptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.SQLBoardDAOImpl.MAPPER_BOARD_NAME;
import static com.epam.coopaint.dao.impl.SQLBoardDAOImpl.MAPPER_BOARD_UUID;
import static com.epam.coopaint.dao.impl.SQLChatDAOImpl.MAPPER_CHAT_UUID;
import static com.epam.coopaint.dao.impl.SQLColumns.COLUMN_SNAP_LINK;

class SnapshotDAOImpl extends GenericDAO implements SnapshotDAO {
    private static final String QUERY_CREATE_SNAPSHOT = "INSERT INTO snapshot (snap_link, chat_id, board_id) VALUES (?, " +
            "(SELECT chat_id FROM chat WHERE chat_uuid=?)," +
            " (SELECT board_id FROM board WHERE board_uuid=?))";
    private static final String QUERY_READ_SNAPSHOT = "SELECT snap_link, c.chat_uuid, b.board_uuid FROM snapshot " +
            "INNER JOIN chat c ON snapshot.chat_id = c.chat_id " +
            "INNER JOIN board b on snapshot.board_id = b.board_id WHERE snap_link=?";
    private static final String QUERY_READ_SNAPSHOTS_BY_USER_UUID = "SELECT snap_link, c.chat_uuid, b.board_uuid, b.board_name " +
            "FROM snapshot " +
            "INNER JOIN user u ON u.user_uuid = ? " +
            "INNER JOIN chat c ON snapshot.chat_id = c.chat_id " +
            "INNER JOIN board b on snapshot.board_id = b.board_id";

    static RsToObject<Snapshot> MAPPER_SNAPSHOT_LINK = (rs, sn) -> sn.setLink(rs.getString(COLUMN_SNAP_LINK));
    static RsToObject<Snapshot> MAPPER_SNAPSHOT_CHAT_UUID = (rs, sn) -> MAPPER_CHAT_UUID.apply(rs, sn.getChat());
    static RsToObject<Snapshot> MAPPER_SNAPSHOT_BOARD_UUID = (rs, sn) -> MAPPER_BOARD_UUID.apply(rs, sn.getBoard());
    static RsToObject<Snapshot> MAPPER_SNAPSHOT_BOARD_NAME = (rs, sn) -> MAPPER_BOARD_NAME.apply(rs, sn.getBoard());

    public Snapshot readSnapshot(String link) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_READ_SNAPSHOT)) {
            preparedStatement.setString(1, link);
            try (ResultSet result = preparedStatement.executeQuery()) {
                RsToObjectListMapper<Snapshot> mapper = new RsToObjectListMapper<>(List.of(
                        MAPPER_SNAPSHOT_LINK,
                        MAPPER_SNAPSHOT_CHAT_UUID,
                        MAPPER_SNAPSHOT_BOARD_UUID
                ));
                List<Snapshot> snapshots = mapper.mapToList(result, Snapshot::new);
                if (!snapshots.isEmpty()) {
                    return snapshots.get(0);
                } else {
                    throw new DAOException("No such snapshot: " + link);
                }
            } catch (Exception e) {
                throw new DAOException("Failed to map snapshot by link: " + link, e);
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to read snapshot: " + link, e);
        }
    }

    @Override
    public Snapshot createSnapshot(Snapshot snapshot) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_CREATE_SNAPSHOT)) {
            statement.setString(1, snapshot.getLink());
            statement.setBytes(2, Encryptor.uuidToBytes(snapshot.getChat().getUuid()));
            statement.setBytes(3, Encryptor.uuidToBytes(snapshot.getBoard().getUuid()));
            statement.execute();
            return snapshot;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Snapshot> readSnapshots(UUID userUUID) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_READ_SNAPSHOTS_BY_USER_UUID)) {
            preparedStatement.setBytes(1, Encryptor.uuidToBytes(userUUID));
            try (ResultSet result = preparedStatement.executeQuery()) {
                var snap = new Snapshot();
                RsToObjectListMapper<Snapshot> mapper = new RsToObjectListMapper<>(List.of(
                        MAPPER_SNAPSHOT_LINK,
                        MAPPER_SNAPSHOT_CHAT_UUID,
                        MAPPER_SNAPSHOT_BOARD_UUID,
                        MAPPER_SNAPSHOT_BOARD_NAME));
                List<Snapshot> snapshots = mapper.mapToList(result, Snapshot::new);
                return snapshots;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}
