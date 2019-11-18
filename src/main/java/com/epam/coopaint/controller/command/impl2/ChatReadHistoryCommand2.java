package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

public class ChatReadHistoryCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        UUID chatUUID = UUID.fromString(props.get(0));
        try {
            String bundle = sessionHandler.readChatHistory(chatUUID);
            return new CommandResult().setBody(bundle);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
