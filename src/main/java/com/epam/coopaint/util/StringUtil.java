package com.epam.coopaint.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String toEnumString(String value) {
        return value
                .toUpperCase()
                .replaceAll("-", "_")
                .replaceAll("/", "");
    }

    public static List<String> parseGroups(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        var properties = new ArrayList<String>();
        if (matcher.find()) {
            for (int i = 1; i < matcher.groupCount(); i++) {
                properties.add(matcher.group(i));
            }
        }
        return properties;
    }
}
