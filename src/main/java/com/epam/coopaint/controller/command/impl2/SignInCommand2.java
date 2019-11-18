package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.ErrorInfo;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.ServiceFactory;
import com.epam.coopaint.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.nio.file.Paths;
import java.util.List;

import static com.epam.coopaint.domain.LocationData.SERVE_PATH_AVATAR;
import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class SignInCommand2 implements Command2 {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        var mapper = new ObjectMapper();
        UserService userService = ServiceFactory.getInstance().getUserService();
        try {
            try {
                SignInUpBundle signInUpBundle = mapper.readValue(body, SignInUpBundle.class);
                User user = userService.singIn(signInUpBundle);
                if (!user.getAvatar().isEmpty()) {
                    user.setAvatar(Paths.get(SERVE_PATH_AVATAR, user.getAvatar()).toString());
                }
                String jsonUser = mapper.writeValueAsString(user);
                session.setAttribute(SESSION_USER, user);
                return new CommandResult().setBody(jsonUser);
            } catch (ServiceException e) {
                var error = new ErrorInfo(SC_NOT_FOUND, "sign.in.error.no.such");
                logger.error("Not registered user tried to sign in.");
                return new CommandResult().setCode(SC_NOT_FOUND).setBody(mapper.writeValueAsString(error));
            }
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
