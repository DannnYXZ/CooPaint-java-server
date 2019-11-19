package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class AuthCommand2 implements Command2 {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var httpSession = (HttpSession) session;
            User user = (User) httpSession.getAttribute(SESSION_USER); // already filtered (at least guest)
            return new CommandResult().setBody(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
