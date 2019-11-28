package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.dao.UserDAO;

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
