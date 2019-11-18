package com.epam.coopaint.controller.command.impl;

import com.epam.coopaint.controller.UploadType;
import com.epam.coopaint.controller.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_UPLOAD_TYPE;

public class UploadSetAvatarCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try (Writer out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                session.setAttribute(SESSION_UPLOAD_TYPE, UploadType.AVATAR);
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
