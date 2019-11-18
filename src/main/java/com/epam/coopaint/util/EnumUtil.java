package com.epam.coopaint.util;

public class EnumUtil {
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String s) {
        try {
            Enum.valueOf(enumClass, s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
