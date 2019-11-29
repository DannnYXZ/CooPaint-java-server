package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.BoardDAO;
import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.dao.SnapshotDAO;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.domain.Room;

import java.util.function.Supplier;

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

    public <R extends Room<E>, E> RoomDAO<R> createRoomDao(Class<E> daoClass) {
        Supplier<RoomDAO> s = SQLBoardDAOImpl::new;
        // Supplier<R> supplier = daoClass;
        return s.get();
    }


    public BoardDAO createBoardDAO() {
        return new SQLBoardDAOImpl();
    }
}
