package com.epam.coopaint.service;

import com.epam.coopaint.dao.impl.SQLChatDAOImpl;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.domain.Message;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WSChatService2 extends WSService<Chat, Message> {
    public WSChatService2() {
        roomDaoSupplier = SQLChatDAOImpl::new;
        roomSupplier = Chat::new;
    }
}
