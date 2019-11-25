package com.epam.coopaint.dao;

import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.DAOException;

public interface SnapshotDAO {
    Snapshot getSnapshot(String link) throws DAOException;
    Snapshot putSnapshot(Snapshot snapshot) throws DAOException;
}
