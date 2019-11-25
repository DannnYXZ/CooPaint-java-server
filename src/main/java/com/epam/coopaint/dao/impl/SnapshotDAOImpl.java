package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.ConnectionPoolException;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.pool.ConnectionPoolImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.epam.coopaint.dao.impl.SQLData.*;

public class SnapshotDAOImpl implements SnapshotDAO {
    private static final String QUERY_GET_SNAPSHOT = "SELECT * FROM snapshot WHERE link=?";

    public Snapshot getSnapshot(String link) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_GET_SNAPSHOT)) {
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
    public Snapshot putSnapshot(Snapshot snapshot) throws DAOException {
        return null;
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
