package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.exception.ConnectionPoolException;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.pool.ConnectionPoolImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {
    private static Logger logger = LogManager.getLogger();
    private Connection proxyConnection;

    public void begin(GenericDAO dao, GenericDAO... daos) throws DAOException {
        try {
            proxyConnection = ConnectionPoolImpl.getInstance().takeConnection();
            proxyConnection.setAutoCommit(false);
            injectConnection(dao, proxyConnection);
            for (GenericDAO d : daos) {
                injectConnection(d, proxyConnection);
            }
        } catch (ConnectionPoolException e) {
            throw new DAOException("Failed to get db connection.", e);
        } catch (SQLException e) {
            throw new DAOException("Failed to create transaction.", e);
        }
    }

    public void commit() throws DAOException {
        try {
            proxyConnection.commit();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public void rollback() {
        try {
            proxyConnection.rollback();
        } catch (SQLException e) {
            logger.error("Failed to rollback transaction.", e);
        }
    }

    public void end() {
        try {
            proxyConnection.setAutoCommit(true);
            proxyConnection.close();
        } catch (SQLException e) {
            logger.error("Failed to close transaction.", e);
        }
    }

    private static void injectConnection(GenericDAO dao, Connection connection) throws DAOException {
        try {
            Field targetField = GenericDAO.class.getDeclaredField("connection");
            targetField.setAccessible(true);
            targetField.set(dao, connection);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DAOException("Failed to inject connection in transactional DAO.", e);
        }
    }
}
