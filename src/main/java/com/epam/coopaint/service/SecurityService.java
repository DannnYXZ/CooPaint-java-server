package com.epam.coopaint.service;

import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;

public interface SecurityService {
    boolean canAccess(String resource, ResourceAction action, User actor) throws ServiceException;
}
