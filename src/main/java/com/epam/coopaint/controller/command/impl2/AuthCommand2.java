package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthCommand2 implements Command2 {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) {
        var out = new CommandResult();
        if (session == null) {
            out.setStatusCode(SC_UNAUTHORIZED);
        } else {
            User user = (User) session.getAttribute(SESSION_USER);
            try {
                out.setBody(new ObjectMapper().writeValueAsString(user));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return out;
    }
}
