package com.epam.coopaint.util;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Properties;

public class PropertyLoaderTest {
    private static String[] propFiles = {
            "props-1.properties",
            "props-2.properties",
            "props-3.properties"
    };

    @BeforeTest
    public void setUp() {
        for (int i = 0; i < propFiles.length; i++) {
            propFiles[i] = PropertyLoaderTest.class.getResource(propFiles[i]).getPath();
        }
    }

    @DataProvider(name = "paths-to-properties")
    public Object[][] createPropertiesData() {
        Properties props1 = new Properties();
        props1.put("a.b.c", "hmmmmmmmmmm");
        props1.put("b.c.d", "yep");
        Properties props2 = new Properties();
        Properties props3 = new Properties();
        props3.put("i.am.your.father", "Dart");
        return new Object[][]{
                {propFiles[0], props1},
                {propFiles[1], props2},
                {propFiles[2], props3},
        };
    }

    @Test(dataProvider = "paths-to-properties")
    public void testSamePasswordDifferentHash(String path, Properties expectedProps) {
        Properties loadedProps = PropertyLoader.loadProperties(path);
        Assert.assertEquals(loadedProps, expectedProps);
    }
}
