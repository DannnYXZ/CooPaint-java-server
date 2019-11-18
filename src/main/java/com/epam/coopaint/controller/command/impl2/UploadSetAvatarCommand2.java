package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.UploadType;
import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_UPLOAD_TYPE;

public class UploadSetAvatarCommand2 implements Command2 {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) {
        session.setAttribute(SESSION_UPLOAD_TYPE, UploadType.AVATAR);
        return new CommandResult();
    }
}
