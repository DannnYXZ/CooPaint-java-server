package com.epam.coopaint.pool;

import com.epam.coopaint.exception.ConnectionPoolException;

import java.sql.Connection;

public interface ConnectionPool {
    Connection takeConnection() throws ConnectionPoolException;
    void releaseConnection(Connection connection);
    public void closeAllConnections();
}
