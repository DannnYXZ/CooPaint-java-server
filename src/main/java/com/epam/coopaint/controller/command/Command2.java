package com.epam.coopaint.controller.command;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface Command2 {
    CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException;
}
