package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

public class ChatConnectCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        String chatUUID = props.get(0); // can be UUID or null
        try {
            UUID result = sessionHandler.connectTo(chatUUID);
            return new CommandResult().setBody(bundle);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
