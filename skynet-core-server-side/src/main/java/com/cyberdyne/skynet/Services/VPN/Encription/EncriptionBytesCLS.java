package com.cyberdyne.skynet.Services.Encription;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class EncriptionBytesCLS
{

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    // Helper method to generate a SecretKeySpec from a passphrase
    private static SecretKeySpec generateSecretKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = sha.digest(keyBytes);
        // Use only first 16 bytes for 128-bit AES key
        byte[] truncatedKey = new byte[16];
        System.arraycopy(keyBytes, 0, truncatedKey, 0, truncatedKey.length);
        return new SecretKeySpec(truncatedKey, "AES");
    }

    // Encrypt method with byte array input
    public static byte[] encrypt(byte[] plainData, String key) throws Exception {
        // Generate key from passphrase
        SecretKeySpec secretKey = generateSecretKey(key);

        // Generate IV
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Encrypt
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plainData);

        // Combine IV and encrypted bytes
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return combined;
    }

    // Decrypt method with byte array output
    public static byte[] decrypt(byte[] encryptedData, String key) throws Exception {
        // Generate key from passphrase
        SecretKeySpec secretKey = generateSecretKey(key);

        // Extract IV (first 16 bytes)
        byte[] iv = new byte[16];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted bytes
        byte[] encryptedBytes = new byte[encryptedData.length - 16];
        System.arraycopy(encryptedData, 16, encryptedBytes, 0, encryptedBytes.length);

        // Decrypt
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(encryptedBytes);
    }

    // Convenience methods that convert between String and byte[]

    // Encrypt String to Base64 String
    public static String encryptToString(String plainText, String key) throws Exception {
        byte[] encryptedBytes = encrypt(plainText.getBytes(StandardCharsets.UTF_8), key);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt Base64 String to String
    public static String decryptFromString(String encryptedText, String key) throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = decrypt(encryptedData, key);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
