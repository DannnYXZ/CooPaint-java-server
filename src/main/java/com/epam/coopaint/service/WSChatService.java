package com.epam.coopaint.service;

import com.epam.coopaint.dao.impl.SQLChatDAOImpl;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.domain.Message;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WSChatService extends WSService<Chat, Message> {
    public WSChatService() {
        roomDaoSupplier = SQLChatDAOImpl::new;
        roomSupplier = Chat::new;
    }
}
