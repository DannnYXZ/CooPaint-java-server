package com.epam.coopaint.command.impl;

import com.epam.coopaint.controller.UploadType;
import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.exception.CommandException;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_UPLOAD_TYPE;

public class UploadSetAvatarCommand implements Command {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var httpSession = (HttpSession) session;
            httpSession.setAttribute(SESSION_UPLOAD_TYPE, UploadType.AVATAR);
            return new CommandResult();
        } catch (ClassCastException e) {
            throw new CommandException();
        }
    }
}
