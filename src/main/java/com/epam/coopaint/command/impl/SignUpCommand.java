package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class SignUpCommand implements Command {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        var mapper = new ObjectMapper();
        try {
            var httpSession = (HttpSession) session;
            SignInUpBundle bundle = mapper.readValue(body, SignInUpBundle.class);
            UserService userService = ServiceFactory.INSTANCE.getUserService();
            List<User> users = userService.getUsersByEmail(bundle.getEmail());
            if (!users.isEmpty()) {
                ObjectNode err = mapper.createObjectNode().put("body", "sign.up.error.exists");
                return new CommandResult().setCode(SC_BAD_REQUEST).setBody(mapper.writeValueAsString(err));
            }
            User user = userService.signUp(bundle);
            httpSession.setAttribute(SESSION_USER, user);
            return new CommandResult().setBody(mapper.writeValueAsString(user));
        } catch (ServiceException | JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
