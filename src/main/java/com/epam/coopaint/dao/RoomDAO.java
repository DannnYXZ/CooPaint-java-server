package com.epam.coopaint.dao;

import com.epam.coopaint.exception.DAOException;

import java.util.List;
import java.util.UUID;

public interface RoomDAO<R> {
    List<R> readUserRoomsMeta(UUID userUUID) throws DAOException;
    R readRoom(UUID roomUUID) throws DAOException;
    R createRoom(R newRoom) throws DAOException;
    R updateRoom(R room) throws DAOException;
    void deleteRoom(UUID roomUUID) throws DAOException;
}
