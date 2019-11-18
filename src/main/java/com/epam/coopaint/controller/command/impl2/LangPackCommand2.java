package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.util.EnumUtil;
import com.epam.coopaint.util.LangPack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;
import java.util.List;

public class LangPackCommand2 implements Command2 {

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        var mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(body);
            String langArg = root.path("lang").asText();
            LangPack langPack = LangPack.EN;
            if (EnumUtil.isValidEnum(LangPack.class, langArg)) {
                langPack = LangPack.valueOf(langArg);
            }
            return new CommandResult().setBody(langPack.getContent());
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
