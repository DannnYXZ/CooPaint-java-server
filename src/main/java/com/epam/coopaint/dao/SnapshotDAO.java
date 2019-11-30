package com.epam.coopaint.dao;

import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.DAOException;

import java.util.List;
import java.util.UUID;

public interface SnapshotDAO {
    Snapshot readSnapshot(String link) throws DAOException;
    Snapshot createSnapshot(Snapshot snapshot) throws DAOException;
    List<Snapshot> readSnapshots(UUID userUUID) throws DAOException;
}
