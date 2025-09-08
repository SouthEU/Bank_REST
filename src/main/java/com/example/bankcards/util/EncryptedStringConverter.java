package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    @Value("${app.encryption.key}")
    private String keyHex;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return encrypt(attribute, Hex.decode(keyHex));
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return decrypt(dbData, Hex.decode(keyHex));
    }

    private String encrypt(String plainText, byte[] key) {
        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] cipherText = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, cipherText, 0, iv.length);
            System.arraycopy(encrypted, 0, cipherText, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    private String decrypt(String encryptedData, byte[] key) {
        try {
            byte[] cipherData = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[12];
            System.arraycopy(cipherData, 0, iv, 0, iv.length);
            byte[] encrypted = new byte[cipherData.length - iv.length];
            System.arraycopy(cipherData, iv.length, encrypted, 0, encrypted.length);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }
}
