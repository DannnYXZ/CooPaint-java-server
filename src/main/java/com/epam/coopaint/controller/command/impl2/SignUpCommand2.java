package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.ServiceFactory;
import com.epam.coopaint.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class SignUpCommand2 implements Command2 {

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        var mapper = new ObjectMapper();
        try {
            try {
                SignInUpBundle bundle = mapper.readValue(body, SignInUpBundle.class);
                UserService clientService = ServiceFactory.getInstance().getUserService();
                User user = clientService.signUp(bundle);
                session.setAttribute(SESSION_USER, user); // TODO: check storage
                return new CommandResult().setBody(mapper.writeValueAsString(user));
                // MailSender.getInstance().sendMail("Welcome to CooPaint, " + user.getName(), user.getEmail());
            } catch (ServiceException e) {
                return new CommandResult().setCode(SC_BAD_REQUEST).setBody("sign.up.error.exists");
            }
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
