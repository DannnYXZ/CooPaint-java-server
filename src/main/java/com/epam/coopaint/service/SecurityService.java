package com.epam.coopaint.service;

import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.UserAction;
import com.epam.coopaint.exception.ServiceException;

public interface SecurityService {
    boolean canAccess(String resource, UserAction action, User actor) throws ServiceException;
}
