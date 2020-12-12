package com.epam.coopaint.util;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public enum LangPack {
    RU("RU.json"),
    EN("EN.json");

    private String langPack;

    LangPack(String localeFileName) {
        try {
            URL localeURL = LangPack.class.getResource(localeFileName);
            InputStream in = localeURL.openStream();
            langPack = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locale " + localeFileName, e);
        }
    }

    public String getContent() {
        return langPack;
    }
}
