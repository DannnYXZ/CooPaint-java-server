package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;

public class AuthCommand implements Command {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var httpSession = (HttpSession) session;
            // user already filtered (at least guest)
            User user = (User) httpSession.getAttribute(SESSION_USER);
            return new CommandResult().setBody(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
