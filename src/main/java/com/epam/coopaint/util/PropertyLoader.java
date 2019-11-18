package com.epam.coopaint.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
    public static Properties loadProperties(String path) {
        try (InputStream in = new FileInputStream(path)) {
            var properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from: " + path, e);
        }
    }
}

