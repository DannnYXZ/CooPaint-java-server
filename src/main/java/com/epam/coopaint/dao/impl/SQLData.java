package com.epam.coopaint.dao.impl;

import com.epam.coopaint.domain.Board;
import com.epam.coopaint.exception.DAOException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

interface RsToObjectMapper<T> {
    void apply(ResultSet rs, T target) throws Exception;
}

class SQLData {
    static final String COLUMN_USER_ID = "id";
    static final String COLUMN_USER_NAME = "name";
    static final String COLUMN_USER_EMAIL = "email";
    static final String COLUMN_USER_HASH = "hash";
    static final String COLUMN_USER_SALT = "salt";
    static final String COLUMN_USER_AVATAR = "avatar";
    static final String COLUMN_USER_LANG = "lang";

    static final String COLUMN_BOARD_ID = "id";
    static final String COLUMN_BOARD_UUID = "uuid";
    static final String COLUMN_BOARD_NAME = "name";
    static final String COLUMN_BOARD_DATA = "data";

    static final String COLUMN_CHAT_ID = "id";
    static final String COLUMN_CHAT_UUID = "uuid";
    static final String COLUMN_CHAT_DATA = "data";

    static final String COLUMN_ACL_GROUP = "group";

    static final String COLUMN_SNAP_LINK = "link";
    static final String COLUMN_SNAP_CHAT_ID = "chat_id";
    static final String COLUMN_SNAP_BOARD_ID = "board_id";

}
