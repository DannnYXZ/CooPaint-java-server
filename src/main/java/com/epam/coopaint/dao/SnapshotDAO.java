package com.epam.coopaint.dao;

import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.DAOException;

public interface SnapshotDAO {
    Snapshot readSnapshot(String link) throws DAOException;
    Snapshot createSnapshot(Snapshot snapshot) throws DAOException;
}
