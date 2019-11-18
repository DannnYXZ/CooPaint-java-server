package com.epam.coopaint.util;

import java.io.InputStream;
import java.net.URL;

public enum LangPack {
    RU("RU.json"),
    EN("EN.json"),
    DE("DE.json");

    private String langPack;

    LangPack(String localeFileName) {
        try {
            URL localeURL = LangPack.class.getResource(localeFileName);
            InputStream in = localeURL.openStream();
            langPack = new String(in.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locale " + localeFileName, e);
        }
    }

    public String getContent() {
        return langPack;
    }
}
