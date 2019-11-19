package com.epam.coopaint.service;

import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;

import java.util.List;

public interface UserService {
    User signUp(SignInUpBundle bundle) throws ServiceException;
    User singIn(SignInUpBundle bundle) throws ServiceException;
    List<User> getUsersByEmail(String email) throws ServiceException;
    User createGuest();
    void singOut(String login) throws ServiceException;
    void updateAvatar(long userId, String newAvatarFileName) throws ServiceException;
}