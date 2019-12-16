package com.epam.coopaint.pool;

import com.epam.coopaint.exception.ConnectionPoolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.epam.coopaint.pool.DBConnectionData.*;

public final class ConnectionPoolImpl implements ConnectionPool {
    private static Logger logger = LogManager.getLogger();
    private final Lock lock;
    private final Semaphore semaphore;
    private Queue<ProxyConnection> connectionPool;
    private static ConnectionPool instance;

    static {
        try {
            instance = new ConnectionPoolImpl();
        } catch (SQLException e) {
            throw new RuntimeException("Database is down.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database driver.", e);
        }
    }

    public static ConnectionPool getInstance() {
        return instance;
    }

    private ConnectionPoolImpl() throws SQLException, ClassNotFoundException {
        connectionPool = new ArrayDeque<>(DB_CONNECTIONS_MAX);
        Class.forName(DB_DRIVER);
        for (int i = 0; i < DB_CONNECTIONS_MAX; i++) {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connectionPool.add(new ProxyConnection(connection));
        }
        semaphore = new Semaphore(DB_CONNECTIONS_MAX);
        lock = new ReentrantLock();
    }


    @Override
    public Connection takeConnection() throws ConnectionPoolException {
        try {
            semaphore.acquire();
            lock.lock();
            Connection connection = connectionPool.poll();
            if (!connection.isValid(DB_CONNECTION_CHECK_TIME_S)) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return new ProxyConnection(connection);
        } catch (InterruptedException e) {
            throw new ConnectionPoolException("Database connection acquiring interrupted.", e);
        } catch (SQLException e) {
            throw new ConnectionPoolException("Error while trying to obtain database connection.", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseConnection(Connection connection) {
        if (connection.getClass() != ProxyConnection.class) {
            logger.warn("Attempt to add wild connection into pool.");
            return; // TODO: throw runtime for bad user logic to be fixed?
        }
        var proxyConnection = (ProxyConnection) connection;
        lock.lock();
        connectionPool.add(proxyConnection);
        lock.unlock();
        semaphore.release();
    }

    // clear pool on destroy
    public void closeAllConnections() {
        for (int i = 0; i < DB_CONNECTIONS_MAX; i++) {
            try {
                var connection = (ProxyConnection) takeConnection();
                connection.reallyClose();
                logger.info("Connection " + (i + 1) + " closed.");
            } catch (Exception e) {
                logger.error("Error while closing connection.", e);
            }
        }
        deregisterDrivers();
    }

    private void deregisterDrivers() {
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.error("Failed to deregister driver", e);
            }
        });
    }
}
