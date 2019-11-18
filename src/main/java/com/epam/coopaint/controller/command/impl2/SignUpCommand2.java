package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.ErrorInfo;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.ServiceFactory;
import com.epam.coopaint.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class SignUpCommand2 implements Command2 {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) {
        CommandResult out = new CommandResult();
        var mapper = new ObjectMapper();
        SignInUpBundle bundle = null;
        try {
            bundle = mapper.readValue(body, SignInUpBundle.class);
            UserService clientService = ServiceFactory.getInstance().getUserService();
            User user = clientService.signUp(bundle);
            session.setAttribute(SESSION_USER, user); // TODO: check storage
            out.setBody(mapper.writeValueAsString(user));
            // MailSender.getInstance().sendMail("Welcome to CooPaint, " + user.getName(), user.getEmail());
        } catch (ServiceException e) {
            var error = new ErrorInfo();
            error.setCode(HttpServletResponse.SC_BAD_REQUEST);
            error.setMessage("sign.up.error.exists");
            try {
                out.setBody(mapper.writeValueAsString(error));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
            logger.error(error.getMessage(), e);
            out.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return out;
    }
}
