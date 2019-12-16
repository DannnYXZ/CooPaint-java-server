package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.*;
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

    public FileSystemDAO createFileSystemDAO() {
        return new FileSystemDAOImpl();
    }

    public <R extends Room<E>, E> RoomDAO<R> createRoomDao(Class<E> daoClass) {
        Supplier<RoomDAO> s = SQLBoardDAOImpl::new;
        // Supplier<R> supplier = daoClass;
        return s.get();
    }
}
