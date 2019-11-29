package com.epam.coopaint.dao;

import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.Room;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;

import java.util.List;
import java.util.UUID;

public interface BoardDAO {
    void getRights(User user, long accessRights);
    void setRights(User user, long accessRights);
}
