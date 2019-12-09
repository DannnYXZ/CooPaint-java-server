package com.epam.coopaint.dao;

import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;

import java.util.List;
import java.util.UUID;

public interface UserDAO {
    User signIn(SignInUpBundle bundle) throws DAOException;
    User getUser(UUID uuid) throws DAOException;
    void update(User updater) throws DAOException;
    List<User> getUsers(String email) throws DAOException;
    void signUp(SignInUpBundle bundle) throws DAOException;
    void updateAvatar(long userId, String newAvatarPath) throws DAOException;
}
