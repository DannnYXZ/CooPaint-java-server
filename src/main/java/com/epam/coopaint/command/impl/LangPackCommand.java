package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.epam.coopaint.util.EnumUtil;
import com.epam.coopaint.util.LangPack;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class LangPackCommand implements Command {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        String langArg = !props.isEmpty() ? props.get(0) : "";
        LangPack langPack = LangPack.EN;
        if (EnumUtil.isValidEnum(LangPack.class, langArg)) {
            langPack = LangPack.valueOf(langArg);
        }
        var httpSession = (HttpSession) session;
        var user = (User) httpSession.getAttribute(SESSION_USER);
        if (user.isAuth() && !langPack.equals(user.getLang())) {
            UserService userService = ServiceFactory.getInstance().getUserService();
            var updater = new User().setLang(langPack).setUuid(user.getUuid());
            try {
                userService.update(updater);
            } catch (ServiceException e) {
                throw new CommandException("Failed to save users language: " + updater.getUuid());
            }
        }
        return new CommandResult().setBody(langPack.getContent());
    }
}
