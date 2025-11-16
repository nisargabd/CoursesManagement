package com.sanketika.course_backend.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.*;
import java.util.Base64;

public class PemUtils {

    private static String readKey(String path) throws Exception {
        InputStream is = PemUtils.class.getClassLoader().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Key file not found: " + path);
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static byte[] parsePEM(String pem) {
        String normalized = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(normalized);
    }

    public static PrivateKey loadPrivateKey(String path) throws Exception {
        String pem = readKey(path);
        byte[] bytes = parsePEM(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String path) throws Exception {
        String pem = readKey(path);
        byte[] bytes = parsePEM(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
