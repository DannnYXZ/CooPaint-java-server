package com.epam.coopaint.service;

import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User signUp(SignInUpBundle bundle) throws ServiceException;
    User singIn(SignInUpBundle bundle) throws ServiceException;
    List<User> getUsersByEmail(String email) throws ServiceException;
    User createGuest();
    User update(User updater) throws ServiceException;
    void updateAvatar(UUID userUUID, String newAvatarFileName) throws ServiceException;
}