package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.impl.ServiceFactory;

import java.util.List;
import java.util.UUID;

public class BoardDeleteCommand implements Command {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UUID boardUUID = UUID.fromString(props.get(0));
        try {
            var boardService = ServiceFactory.INSTANCE.getBoardService();
            boardService.deleteRoom(boardUUID);
            return new CommandResult();
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
