package com.epam.coopaint.command;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;

import java.util.List;

public interface Command {
    CommandResult execute(List<String> props, String body, Object session) throws CommandException;
}
