package com.turinmachin.unilife.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String hashTokenSHA256(final String token) {
        return hashToken(token, "SHA-256");
    }

    private static String hashToken(final String token, final String algorithm) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);
            final byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            final StringBuilder sb = new StringBuilder();
            for (final byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not supported: " + algorithm, e);
        }
    }

}
