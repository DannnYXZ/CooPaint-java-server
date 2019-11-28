package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.util.Encryptor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SnapshotServiceImpl implements SnapshotService {
    private static final int SNAPSHOT_LINK_SIZE = 13;
    private Map<String, Snapshot> virtualStorage = new ConcurrentHashMap<>(); // <resource, ACL> - offloading DB

    public Snapshot createSnapshot(UUID chat, UUID board, boolean useStorage) throws ServiceException {
        String link = Encryptor.getInstance().generateRandomHash(SNAPSHOT_LINK_SIZE);
        var newSnapshot = new Snapshot().setLink(link).setChatID(chat).setBoardID(board);
        virtualStorage.put(link, newSnapshot);
        if (useStorage) {
            SnapshotDAO snapshotDAO = DAOFactory.getInstance().getSnapshotDAO();
            try {
                snapshotDAO.putSnapshot(newSnapshot);
            } catch (DAOException e) {
                throw new ServiceException("Failed to save snapshot to storage.", e);
            }
        }
        return newSnapshot;
    }

    public Snapshot getSnapshot(String link) throws ServiceException {
        SnapshotDAO snapshotDAO = DAOFactory.getInstance().getSnapshotDAO();
        try {
            if (virtualStorage.containsKey(link)) {
                return virtualStorage.get(link);
            }
            Snapshot snapshot = snapshotDAO.getSnapshot(link);
            virtualStorage.put(link, snapshot);
            return snapshot;
        } catch (DAOException e) {
            throw new ServiceException("Failed to get snapshot data.", e);
        }
    }
}
