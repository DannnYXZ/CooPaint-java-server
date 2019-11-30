package com.epam.coopaint.controller.command.impl;

import com.epam.coopaint.controller.command.Command;
import com.epam.coopaint.domain.ErrorInfo;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class SignUpCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try (Writer out = response.getWriter()) {
            var mapper = new ObjectMapper();
            SignInUpBundle bundle = mapper.readValue(request.getReader(), SignInUpBundle.class);
            UserService clientService = ServiceFactory.getInstance().getUserService();
            try {
                User user =  clientService.signUp(bundle);
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_USER, user); // TODO: check storage
                out.write(mapper.writeValueAsString(user));
                // MailSender.getInstance().sendMail("Welcome to CooPaint, " + user.getName(), user.getEmail());
            } catch (ServiceException e) {
                var error = new ErrorInfo();
                error.setCode(HttpServletResponse.SC_BAD_REQUEST);
                error.setMessage("Such user already exists.");
                out.write(mapper.writeValueAsString(error));
                logger.error(error.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IOException e) {
            logger.error("ErrorInfo while signing up.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
