package com.epam.coopaint.controller.command;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.service.WSChatService;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.List;

public abstract class WSCommand implements Command2 {
    @Inject
    protected WSChatService chatService;

    public WSCommand() {
        this.chatService = CDI.current().select(WSChatService.class).get();
    }

    @Override
    abstract public CommandResult execute(List<String> props, String body, Object session) throws CommandException;
}
