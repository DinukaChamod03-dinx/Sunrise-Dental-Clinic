package com.sunrise.dental.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple SHA-256 based password hashing utility.
 * Passwords are never stored or compared in plain text.
 */
public class PasswordUtil {

    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
