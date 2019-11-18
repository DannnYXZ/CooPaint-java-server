package com.epam.coopaint.service;

import com.epam.coopaint.service.impl.FileSystemServiceImpl;
import com.epam.coopaint.service.impl.SecurityServiceImpl;
import com.epam.coopaint.service.impl.UserServiceImpl;

public final class ServiceFactory {
    private static final ServiceFactory instance = new ServiceFactory();
    private final UserService userService = new UserServiceImpl();
    private final SecurityService securityService = new SecurityServiceImpl();
    private final FileSystemService fileSystemService = new FileSystemServiceImpl();

    private ServiceFactory() {
    }

    public static ServiceFactory getInstance() {
        return instance;
    }

    public UserService getUserService() {
        return userService;
    }

    public FileSystemService getFileSystemService() {
        return fileSystemService;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }
}
