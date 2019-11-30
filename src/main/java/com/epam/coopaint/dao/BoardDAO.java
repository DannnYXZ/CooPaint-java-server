package com.epam.coopaint.dao;

import com.epam.coopaint.domain.User;

public interface BoardDAO {
    void getRights(User user, long accessRights);
    void setRights(User user, long accessRights);
}
