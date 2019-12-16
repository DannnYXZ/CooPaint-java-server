package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class UserUpdateCommand implements Command {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        // this command can only be called by registered user (verified by acl)
        var httpSession = (HttpSession) session;
        var user = (User) httpSession.getAttribute(SESSION_USER);
        UserService userService = ServiceFactory.INSTANCE.getUserService();
        try {
            var mapper = new ObjectMapper().disable(MapperFeature.USE_ANNOTATIONS);
            User updater = mapper.readValue(body, User.class);
            mapper.enable(MapperFeature.USE_ANNOTATIONS);
            updater.setUuid(user.getUuid());
            // only email can cause problems - need to let user know the problem
            String updaterEmail = updater.getEmail();
            if (updaterEmail != null && !userService.getUsersByEmail(updaterEmail).isEmpty()) {
                ObjectNode err = mapper.createObjectNode().put("body", "sign.up.error.exists");
                return new CommandResult().setBody(mapper.writeValueAsString(err)).setCode(SC_BAD_REQUEST);
            }
            User updatedUser = userService.update(updater);
            httpSession.setAttribute(SESSION_USER, updatedUser);
            return new CommandResult().setBody(mapper.writeValueAsString(updatedUser));
        } catch (JsonProcessingException | ServiceException e) {
            e.printStackTrace();
            throw new CommandException("Failed to update user.", e);
        }
    }
}
