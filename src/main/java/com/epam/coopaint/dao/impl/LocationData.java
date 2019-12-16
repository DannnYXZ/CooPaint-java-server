package com.epam.coopaint.dao.impl;

import com.epam.coopaint.util.PropertyLoader;

import java.util.Properties;

public class LocationData {
    public static final String STORAGE_PATH_AVATAR;
    public static final String STORAGE_PATH_BOARD;
    public static final String SERVE_PATH_AVATAR;
    public static final String SERVE_PATH_BOARD;

    static {
        Properties props = PropertyLoader.loadProperties(LocationData.class
                .getResource("location.properties").getPath());
        STORAGE_PATH_AVATAR = props.getProperty("storage.path.avatar");
        STORAGE_PATH_BOARD = props.getProperty("storage.path.board");
        SERVE_PATH_AVATAR = props.getProperty("serve.path.avatar");
        SERVE_PATH_BOARD = props.getProperty("serve.path.board");
    }
}
