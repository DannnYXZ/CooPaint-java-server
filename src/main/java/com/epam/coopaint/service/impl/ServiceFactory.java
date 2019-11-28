package com.epam.coopaint.service.impl;

import com.epam.coopaint.service.FileSystemService;
import com.epam.coopaint.service.SecurityService;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.service.UserService;

public final class ServiceFactory {
    private static final ServiceFactory instance = new ServiceFactory();
    private final UserService userService = new UserServiceImpl();
    private final SecurityService securityService = new SecurityServiceImpl();
    private final FileSystemService fileSystemService = new FileSystemServiceImpl();
    private final SnapshotService snapshotService = new SnapshotServiceImpl();

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

    public SnapshotService getSnapshotService() {
        return snapshotService;
    }
}
