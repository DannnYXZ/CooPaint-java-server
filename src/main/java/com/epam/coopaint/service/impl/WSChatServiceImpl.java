package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.impl.SQLChatDAOImpl;
import com.epam.coopaint.domain.Chat;
import com.epam.coopaint.domain.Message;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WSChatServiceImpl extends WSServiceImpl<Chat, Message> {
    public WSChatServiceImpl() {
        roomDaoSupplier = SQLChatDAOImpl::new;
        roomSupplier = Chat::new;
    }
}
