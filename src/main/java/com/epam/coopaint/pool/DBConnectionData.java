package com.epam.coopaint.pool;

import com.epam.coopaint.util.PropertyLoader;

import java.util.Properties;

class DBConnectionData {
    static final String DB_DRIVER;
    static final String DB_URL;
    static final String DB_USER;
    static final String DB_PASSWORD;
    static final int DB_CONNECTIONS_MAX;
    static final int DB_CONNECTION_CHECK_TIME_S;

    static {
        try {
            Properties props = PropertyLoader.loadProperties(DBConnectionData.class.getResource("db.properties").getPath());
            DB_DRIVER = props.getProperty("db.driver");
            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
            DB_CONNECTIONS_MAX = Integer.parseInt(props.getProperty("db.connections.max"));
            DB_CONNECTION_CHECK_TIME_S = Integer.parseInt(props.getProperty("db.connection.check.time.sec"));
        } catch (Exception e) { // | FIXME
            throw new RuntimeException("Failed to initialize db connection data", e);
        }
    }
}