package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.WSBoardService2;
import com.epam.coopaint.service.WSChatService2;

import javax.enterprise.inject.spi.CDI;
import java.util.List;
import java.util.UUID;

public class BoardDeleteCommand2 implements Command2 {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UUID boardUUID = UUID.fromString(props.get(0));
        try {
            var boardService = CDI.current().select(WSBoardService2.class).get();
            boardService.deleteRoom(boardUUID);
            return new CommandResult();
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
