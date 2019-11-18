package com.epam.coopaint.controller.command.impl;

import com.epam.coopaint.controller.command.Command;
import com.epam.coopaint.util.EnumUtil;
import com.epam.coopaint.util.LangPack;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

public class LangPackCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try (Writer out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            var mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request.getReader());
            String langArg = root.path("lang").asText();
            LangPack langPack;
            if (EnumUtil.isValidEnum(LangPack.class, langArg)) {
                langPack = LangPack.valueOf(langArg);
            } else {
                langPack = LangPack.EN;
            }
            out.write(langPack.getContent());
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
