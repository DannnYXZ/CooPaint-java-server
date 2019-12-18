package com.epam.coopaint.command;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;

import java.util.List;

/**
 * Command
 */
public interface Command {
    /**
     * Execute command
     * @param props extracted from url
     * @param body request body
     * @param session can be any type of session (WebSocket, HTTP), specific for command implementation
     * @return response content
     */
    CommandResult execute(List<String> props, String body, Object session) throws CommandException;
}
