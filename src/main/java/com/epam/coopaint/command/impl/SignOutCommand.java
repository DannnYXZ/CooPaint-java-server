package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;

public class SignOutCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var httpSession = (HttpSession) session;
            logger.info(((User) httpSession.getAttribute(SESSION_USER)).getName() + " logged out.");
            httpSession.invalidate(); // user will be guest on next request
            return new CommandResult();
        } catch (ClassCastException e) {
            throw new CommandException(e);
        }
    }
}
