package com.epam.coopaint.service;

import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.ServiceException;

import java.util.List;
import java.util.UUID;

public interface SnapshotService {
    Snapshot createSnapshot(UUID chat, UUID board, boolean useStorage) throws ServiceException;
    Snapshot readSnapshot(String link) throws ServiceException;
    List<Snapshot> readSnapshots(UUID userUUID) throws ServiceException;
}
