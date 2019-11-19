package com.epam.coopaint.controller.command;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.service.WSChatService;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.List;

public abstract class WSCommand implements Command2 {
    @Inject
    protected WSChatService sessionHandler;

    public WSCommand() {
        this.sessionHandler = CDI.current().select(WSChatService.class).get();
    }

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        return null;
    }
}
