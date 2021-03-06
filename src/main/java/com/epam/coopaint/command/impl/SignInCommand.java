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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class SignInCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        UserService userService = ServiceFactory.INSTANCE.getUserService();
        try {
            var mapper = new ObjectMapper();
            try {
                SignInUpBundle signInUpBundle = mapper.readValue(body, SignInUpBundle.class);
                User user = userService.singIn(signInUpBundle);
                var httpSession = (HttpSession) session;
                String jsonUser = mapper.writeValueAsString(user);
                httpSession.setAttribute(SESSION_USER, user);
                return new CommandResult().setBody(jsonUser);
            } catch (ServiceException e) {
                ObjectNode err = mapper.createObjectNode().put("body", "sign.in.error.no.such");
                logger.error("Not registered user tried to sign in.", e);
                return new CommandResult().setCode(SC_NOT_FOUND).setBody(mapper.writeValueAsString(err));
            }
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
