package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.ConnectionPoolException;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.pool.ConnectionPoolImpl;
import com.epam.coopaint.util.Encryptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.epam.coopaint.dao.impl.SQLData.*;

class SnapshotDAOImpl extends GenericDAO implements SnapshotDAO {
    private static final String QUERY_CREATE_SNAPSHOT = "INSERT INTO snapshot (link, board_id, chat_id) VALUES (?, " +
            "(SELECT id FROM chat WHERE uuid=?)," +
            " (SELECT id FROM board WHERE uuid=?))";
    private static final String QUERY_READ_SNAPSHOT = "SELECT * FROM snapshot WHERE link=?";

    public Snapshot readSnapshot(String link) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_READ_SNAPSHOT)) {
            preparedStatement.setString(1, link);
            try (ResultSet result = preparedStatement.executeQuery()) {
                List<Snapshot> snapshots = mapToSnapshotList(result);
                if (!snapshots.isEmpty()) {
                    return snapshots.get(0);
                } else {
                    throw new DAOException("No such snapshot: " + link);
                }
            }
        } catch (SQLException | ConnectionPoolException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Snapshot createSnapshot(Snapshot snapshot) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_CREATE_SNAPSHOT)) {
            statement.setString(1, snapshot.getLink());
            statement.setBytes(2, Encryptor.uuidToBytes(snapshot.getChatUUID()));
            statement.setBytes(3, Encryptor.uuidToBytes(snapshot.getBoardUUID()));
            statement.execute();
            return snapshot;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private List<Snapshot> mapToSnapshotList(ResultSet resultSet) throws SQLException {
        List<Snapshot> snapshots = new ArrayList<>();
        while (resultSet.next()) {
            var snap = new Snapshot();
            snap.setLink(resultSet.getString(COLUMN_SNAP_LINK));
            snap.setChatID(resultSet.getObject(COLUMN_SNAP_CHAT_ID, java.util.UUID.class));
            snap.setBoardID(resultSet.getObject(COLUMN_SNAP_BOARD_ID, java.util.UUID.class));
            snapshots.add(snap);
        }
        return snapshots;
    }
}
