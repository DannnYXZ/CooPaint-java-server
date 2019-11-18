package com.epam.coopaint.dao;

import com.epam.coopaint.dao.impl.SQLUserDAO;
import com.epam.coopaint.dao.impl.SecurityDAOImpl;

public final class DAOFactory {
    private static DAOFactory instance = new DAOFactory();
    private final UserDAO sqlUserDAOImpl = new SQLUserDAO();
    private final SecurityDAO securityDAOImpl = new SecurityDAOImpl();

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
}
