package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.exception.ServiceException;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

public class ChatReadHistoryCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) {
        try {
            UUID chatUUID = UUID.fromString(props.get(0));
            sessionHandler.readChatHistory(chatUUID);
            return new CommandResult(body);
        } catch (ServiceException e) {
            throw new RuntimeException("kek", e);
        }
    }
}
