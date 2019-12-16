package com.epam.coopaint.service.impl;

import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.domain.Message;
import com.epam.coopaint.domain.VShape;
import com.epam.coopaint.service.SecurityService;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.WSService;

import javax.enterprise.inject.spi.CDI;

public enum ServiceFactory {
    INSTANCE;
    private final UserService userService = new UserServiceImpl();
    private final SecurityService securityService = new SecurityServiceImpl();
    private final SnapshotService snapshotService = new SnapshotServiceImpl();

    public UserService getUserService() {
        return userService;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public SnapshotService getSnapshotService() {
        return snapshotService;
    }

    public WSService<Board, VShape> getBoardService() {
        return CDI.current().select(WSBoardServiceImpl.class).get();
    }

    public WSService<Chat, Message> getChatService() {
        return CDI.current().select(WSChatServiceImpl.class).get();
    }
}
