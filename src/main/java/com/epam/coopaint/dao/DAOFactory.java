package com.epam.coopaint.dao;

import com.epam.coopaint.dao.impl.SQLUserDAOImpl;
import com.epam.coopaint.dao.impl.SecurityDAOImpl;
import com.epam.coopaint.dao.impl.SnapshotDAOImpl;

public final class DAOFactory {
    private static DAOFactory instance = new DAOFactory();
    private final UserDAO sqlUserDAOImpl = new SQLUserDAOImpl();
    private final SecurityDAO securityDAOImpl = new SecurityDAOImpl();
    private final SnapshotDAO snapshotDAOImpl = new SnapshotDAOImpl();

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        return instance;
    }

    public UserDAO getUserDAO() {
        return sqlUserDAOImpl;
    }

    public SecurityDAO getSecurityDAO() {
        return securityDAOImpl;
    }

    public SnapshotDAO getSnapshotDAO() {
        return snapshotDAOImpl;
    }
}
