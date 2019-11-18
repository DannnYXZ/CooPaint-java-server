package com.epam.coopaint.controller;

import com.epam.coopaint.controller.command.Command;
import com.epam.coopaint.controller.command.CommandName;
import com.epam.coopaint.controller.command.impl.*;
import com.epam.coopaint.util.EnumUtil;

import java.util.EnumMap;
import java.util.Map;

final class CommandProvider {
    private final Map<CommandName, Command> repository = new EnumMap<>(CommandName.class);

    CommandProvider() {
        repository.put(CommandName.AUTH, new AuthCommand());
        repository.put(CommandName.SIGN_UP, new SignUpCommand());
        repository.put(CommandName.SIGN_IN, new SignInCommand());
        repository.put(CommandName.SIGN_OUT, new SignOutCommand());
        repository.put(CommandName.WRONG_REQUEST, new WrongRequestCommand());
        repository.put(CommandName.LANG_PACK, new LangPackCommand());
        repository.put(CommandName.SAVE_BOARD, new SaveBoardCommand());
        repository.put(CommandName.SET_AVATAR, new UploadSetAvatarCommand());
    }

    Command getCommand(String name) {
        if (EnumUtil.isValidEnum(CommandName.class, name)) {
            return repository.get(CommandName.valueOf(name));
        } else {
            return repository.get(CommandName.WRONG_REQUEST);
        }
    }
}
