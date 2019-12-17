package com.epam.coopaint.validator;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PasswordValidatorTest {
    PasswordValidator validator;

    @BeforeTest
    public void setUp() {
        validator = new PasswordValidator(false, true, true, 4, 255);
    }

    @DataProvider(name = "passwords")
    public Object[][] createSurfaceData() {
        return new Object[][]{
                {"123abC", true},
                {"flippedZ", false},
                {"666salami", false},
                {"abckjah@344sXX", true},
                {"300XiqX", true},
                {"666salami", false},
                {"not today", false},
        };
    }

    @Test(dataProvider = "passwords")
    public void testPasswordValidator(String password, boolean expectedVerdict) {
        boolean actualVerdict = validator.isValidPassword(password);
        Assert.assertEquals(actualVerdict, expectedVerdict);
    }
}
