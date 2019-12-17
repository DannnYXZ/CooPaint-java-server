package com.epam.coopaint.util;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.regex.Pattern;

public class EncryptorTest {
    private static final String REGEX_ALPHANUMERIC = "^[a-zA-Z0-9].*$";

    @DataProvider(name = "hash-randomness")
    public Object[][] passwords() {
        return new Object[][]{
                {"samalama"},
                {"not a password"},
                {"passwd3"}
        };
    }

    @Test(dataProvider = "hash-randomness")
    public void testSamePasswordDifferentHash(String password) {
        Encryptor encryptor = Encryptor.getInstance();
        encryptor.generateDidgest(password);
        byte[] hash1 = encryptor.getCurrentHash();
        encryptor.generateDidgest(password);
        byte[] hash2 = encryptor.getCurrentHash();
        Assert.assertNotEquals(hash1, hash2);
    }

    @Test(invocationCount = 10)
    public void testEncryptorUuidBijection() {
        UUID randomUUID = UUID.randomUUID();
        byte[] uuidBytes = Encryptor.uuidToBytes(randomUUID);
        UUID restoredUUID = Encryptor.bytesToUuid(uuidBytes);
        Assert.assertEquals(randomUUID, restoredUUID);
    }

    @Test(invocationCount = 10)
    public void testEncryptorAlphaNumericFormat() {
        Pattern pattern = Pattern.compile(REGEX_ALPHANUMERIC);
        int length = (int) (Math.random() * 100);
        String scrambledString = Encryptor.generateAlphaNumHash(length);
        Assert.assertTrue(pattern.matcher(scrambledString).matches());
    }

    @Test(invocationCount = 10)
    public void testEncryptorAlphaNumericLength() {
        Pattern pattern = Pattern.compile(REGEX_ALPHANUMERIC);
        int length = (int) (Math.random() * 100);
        String scrambledString = Encryptor.generateAlphaNumHash(length);
        Assert.assertEquals(scrambledString.length(), length);
    }
}
