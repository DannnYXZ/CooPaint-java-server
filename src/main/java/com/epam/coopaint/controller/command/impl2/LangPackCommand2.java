package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.util.EnumUtil;
import com.epam.coopaint.util.LangPack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.List;

public class LangPackCommand2 implements Command2 {
    private static Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) {
        var out = new CommandResult();
        var mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String langArg = root.path("lang").asText();
        LangPack langPack;
        if (EnumUtil.isValidEnum(LangPack.class, langArg)) {
            langPack = LangPack.valueOf(langArg);
        } else {
            langPack = LangPack.EN;
        }
        out.setBody(langPack.getContent());
        return out;
    }
}
