package com.epam.coopaint.controller.command.impl;

import com.epam.coopaint.domain.User;
import com.epam.coopaint.controller.command.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

import static com.epam.coopaint.domain.SessionAttribute.*;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try (Writer out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(SC_UNAUTHORIZED);
            } else {
                User user = (User) session.getAttribute(SESSION_USER);
                out.write(new ObjectMapper().writeValueAsString(user));
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
