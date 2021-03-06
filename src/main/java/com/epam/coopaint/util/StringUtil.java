package com.epam.coopaint.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
  public static String toEnumString(String value) {
    return value.toUpperCase().replaceAll("-", "_").replaceAll("/", "");
  }

  public static List<String> parseGroups(String text, String regex) {
    Matcher matcher = Pattern.compile(regex).matcher(text);
    var properties = new ArrayList<String>();
    if (matcher.find()) {
      for (int i = 0; i < matcher.groupCount(); i++) {
        properties.add(matcher.group(i + 1));
      }
    }
    return properties;
  }

  public static String compactUUID(String standardUUID) {
    return standardUUID.replace("-", "").toUpperCase();
  }

  public static String standardUUID(String compactUUID) {
    return new StringBuilder(compactUUID)
        .insert(20, "-")
        .insert(16, "-")
        .insert(12, "-")
        .insert(8, "-")
        .toString()
        .toLowerCase();
  }
}
