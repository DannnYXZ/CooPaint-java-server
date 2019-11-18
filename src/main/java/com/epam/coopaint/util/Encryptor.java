package com.epam.coopaint.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Encryptor {
    private static Logger logger = LogManager.getLogger();
    private static Encryptor instance = new Encryptor();
    private static String ALPHA_NUM_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
    private SecureRandom random;
    private MessageDigest md;
    private byte[] currentSalt;
    private byte[] currentHash;

    private Encryptor() {
        currentSalt = new byte[16];
        random = new SecureRandom();
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Algorithm not found", e);
            throw new RuntimeException("Sequrity service is down.", e);
        }
    }

    public static Encryptor getInstance() {
        return instance;
    }

    public void generateDidgest(String password) {
        random.nextBytes(currentSalt);
        md.update(currentSalt);
        currentHash = md.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public void generateDidgest(String password, byte[] salt) {
        currentSalt = salt;
        md.update(currentSalt);
        currentHash = md.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] getCurrentSalt() {
        return currentSalt;
    }

    public byte[] getCurrentHash() {
        return currentHash;
    }

    public String generateRandomHash(int length) {
        var sb = new StringBuilder();
        var random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHA_NUM_STRING.charAt(random.nextInt(ALPHA_NUM_STRING.length())));
        }
        return sb.toString();
    }
}
