package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command;
import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class AuthCommand2 implements Command2 {

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        User user = (User) session.getAttribute(SESSION_USER); // already filtered (at least guest)
        try {
            return new CommandResult().setBody(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
