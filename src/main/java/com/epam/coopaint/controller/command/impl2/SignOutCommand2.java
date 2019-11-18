package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;

import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class SignOutCommand2 implements Command2 {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) {
        logger.info(((User) session.getAttribute(SESSION_USER)).getName() + " logged out.");
        session.invalidate(); // user will be guest on next action
        return new CommandResult();
    }
}
