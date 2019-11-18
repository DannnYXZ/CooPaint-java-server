package com.epam.coopaint.controller.command.impl;

import com.epam.coopaint.controller.command.Command;
import com.epam.coopaint.domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class SignOutCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession userSession = request.getSession(false);
        if (userSession != null) {
            logger.info(((User) userSession.getAttribute(SESSION_USER)).getName() + " logged out.");
            userSession.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
