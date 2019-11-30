package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.util.EnumUtil;
import com.epam.coopaint.util.LangPack;

import java.util.List;

public class LangPackCommand2 implements Command2 {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) {
        String langArg = !props.isEmpty() ? props.get(0) : "";
        LangPack langPack = LangPack.EN;
        if (EnumUtil.isValidEnum(LangPack.class, langArg)) {
            langPack = LangPack.valueOf(langArg);
        }
        return new CommandResult().setBody(langPack.getContent());
    }
}
