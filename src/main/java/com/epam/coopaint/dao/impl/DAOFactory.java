package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.dao.UserDAO;

public enum DAOFactory {
    INSTANCE;

    public UserDAO createUserDAO() {
        return new SQLUserDAOImpl();
    }

    public SecurityDAO createSecurityDAO() {
        return new SecurityDAOImpl();
    }

    public SnapshotDAO createSnapshotDAO() {
        return new SnapshotDAOImpl();
    }
}
