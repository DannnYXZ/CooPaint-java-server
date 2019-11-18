package com.epam.coopaint.service;

import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.UserAction;
import com.epam.coopaint.exception.ServiceException;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

public interface SecurityService {
    boolean canAccess(String resource, UserAction action, User actor) throws ServiceException;
}
