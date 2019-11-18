package com.epam.coopaint.dao;

import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;

public interface UserDAO {
    User signIn(SignInUpBundle bundle) throws DAOException;
    User getUser(long id) throws DAOException;
    User getUser(String email) throws DAOException;
    void signUp(SignInUpBundle bundle) throws DAOException;
    void updateAvatar(long userId, String newAvatarPath) throws DAOException;
}
