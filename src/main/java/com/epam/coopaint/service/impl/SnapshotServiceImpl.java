package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.util.Encryptor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class SnapshotServiceImpl implements SnapshotService {
    private static final int SNAPSHOT_LINK_LENGTH = 13;
    private Map<String, Snapshot> virtualStorage = new ConcurrentHashMap<>(); // <resource, ACL> - offloading DB

    public Snapshot createSnapshot(UUID chatUUID, UUID boardUUID, boolean useStorage) throws ServiceException {
        String link = Encryptor.generateAlphaNumHash(SNAPSHOT_LINK_LENGTH);
        var newSnapshot = new Snapshot().setLink(link);
        newSnapshot.getChat().setUuid(chatUUID);
        newSnapshot.getBoard().setUuid(boardUUID);
        virtualStorage.put(link, newSnapshot);
        if (useStorage) {
            var transaction = new TransactionManager();
            SnapshotDAO snapshotDAO = DAOFactory.INSTANCE.createSnapshotDAO();
            try {
                transaction.begin((GenericDAO) snapshotDAO);
                snapshotDAO.createSnapshot(newSnapshot);
                transaction.commit();
            } catch (DAOException e) {
                transaction.rollback();
                throw new ServiceException("Failed to save snapshot to storage.", e);
            } finally {
                transaction.end();
            }
        }
        return newSnapshot;
    }

    public Snapshot readSnapshot(String link) throws ServiceException {
        // check cache
        if (virtualStorage.containsKey(link)) {
            return virtualStorage.get(link);
        }
        // check db
        SnapshotDAO snapshotDAO = DAOFactory.INSTANCE.createSnapshotDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) snapshotDAO);
            Snapshot snapshot = snapshotDAO.readSnapshot(link);
            transaction.commit();
            virtualStorage.put(link, snapshot); // update cache
            return snapshot;
        } catch (DAOException e) {
            throw new ServiceException("Failed to get snapshot data.", e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public List<Snapshot> readSnapshots(UUID userUUID) throws ServiceException {
        SnapshotDAO snapshotDAO = DAOFactory.INSTANCE.createSnapshotDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) snapshotDAO);
            List<Snapshot> snapshots = snapshotDAO.readSnapshots(userUUID);
            return snapshots;
        } catch (DAOException e) {
            throw new ServiceException("Failed to read snapshots of user: " + userUUID);
        }
    }
}
