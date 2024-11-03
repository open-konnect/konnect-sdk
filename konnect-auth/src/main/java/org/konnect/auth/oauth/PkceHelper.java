package org.konnect.auth.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PkceHelper {

    @Getter
    @RequiredArgsConstructor
    public enum Algorithms {
        SHA_256("SHA-256", "S256"),
        NONE("", "");

        private final String javaName;
        private final String protocolName;
    }

    public static String generateCodeVerifier() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String generateCodeChallenge(String codeVerifier, Algorithms algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.getJavaName());
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
