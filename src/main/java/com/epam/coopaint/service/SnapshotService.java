package com.epam.coopaint.service;

import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.ServiceException;

import java.util.List;
import java.util.UUID;

/**
 *  SnapshotService - manages links for board-chat pairs
 *  to be easily retrieved by users
 */
public interface SnapshotService {
    Snapshot createSnapshot(UUID chat, UUID board, boolean useStorage) throws ServiceException;
    Snapshot readSnapshot(String link) throws ServiceException;
    /**
     * Retrieves all chat-board pairs allocated by registered user
     * @param userUUID - user whose snapshots are queried
     * @return - list of user snapshots
     */
    List<Snapshot> readSnapshots(UUID userUUID) throws ServiceException;
}
