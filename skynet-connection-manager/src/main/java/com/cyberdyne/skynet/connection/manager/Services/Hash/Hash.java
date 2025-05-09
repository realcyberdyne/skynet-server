package com.cyberdyne.skynet.connection.manager.Services.Hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for hashing text with various algorithms
 */
public class Hash {

    // Algorithm constants
    public static final String ALGORITHM_MD5 = "MD5";
    public static final String ALGORITHM_SHA1 = "SHA-1";
    public static final String ALGORITHM_SHA256 = "SHA-256";
    public static final String ALGORITHM_SHA512 = "SHA-512";

    // Default values for PBKDF2
    private static final int DEFAULT_ITERATIONS = 10000;
    private static final int DEFAULT_KEY_LENGTH = 256;
    private static final int DEFAULT_SALT_LENGTH = 16;

    /**
     * Hash text using specified algorithm
     *
     * @param text Text to hash
     * @param algorithm Hashing algorithm (MD5, SHA-1, SHA-256, SHA-512)
     * @return Hexadecimal string representation of hash
     */
    public static String hashText(String text, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing text: " + e.getMessage());
            return null;
        }
    }

    /**
     * Hash text using SHA-256 (default algorithm)
     *
     * @param text Text to hash
     * @return Hexadecimal string representation of hash
     */
    public static String hashText(String text) {
        return hashText(text, ALGORITHM_SHA256);
    }

    /**
     * Hash text with salt using specified algorithm
     *
     * @param text Text to hash
     * @param salt Salt value as byte array
     * @param algorithm Hashing algorithm
     * @return Hexadecimal string representation of hash
     */
    public static String hashTextWithSalt(String text, byte[] salt, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(salt);
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing text with salt: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generate a secure hash using PBKDF2 (Password-Based Key Derivation Function 2)
     * Recommended for password storage
     *
     * @param password Password to hash
     * @return Formatted string containing salt, iteration count, and hash
     */
    public static String hashPassword(String password) {
        return hashPassword(password, DEFAULT_ITERATIONS, DEFAULT_KEY_LENGTH);
    }

    /**
     * Generate a secure hash using PBKDF2 with custom parameters
     *
     * @param password Password to hash
     * @param iterations Number of iterations
     * @param keyLength Length of the derived key
     * @return Formatted string containing salt, iteration count, and hash
     */
    public static String hashPassword(String password, int iterations, int keyLength) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[DEFAULT_SALT_LENGTH];
            random.nextBytes(salt);

            // Hash the password
            byte[] hash = pbkdf2(password, salt, iterations, keyLength);

            // Format: iterations:salt:hash
            return iterations + ":" +
                    Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verify a password against a stored hash
     *
     * @param password Password to verify
     * @param storedHash Stored hash from hashPassword()
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash
            String[] parts = storedHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);

            // Generate hash of the provided password
            byte[] testHash = pbkdf2(password, salt, iterations, hash.length * 8);

            // Compare hashes using constant-time comparison
            return constantTimeEquals(hash, testHash);

        } catch (Exception e) {
            System.out.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate a random salt
     *
     * @param length Length of salt in bytes
     * @return Random salt as byte array
     */
    public static byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * HMAC (Hash-based Message Authentication Code) implementation
     *
     * @param text Text to hash
     * @param key Secret key
     * @param algorithm Hash algorithm
     * @return HMAC hash as hexadecimal string
     */
    public static String hmac(String text, String key, String algorithm) {
        try {
            // Simple HMAC implementation for demonstration
            // For production, use javax.crypto.Mac

            // Inner padding
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] ipad = new byte[64];
            byte[] opad = new byte[64];

            // Copy key bytes or hash if too long
            if (keyBytes.length > 64) {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                keyBytes = digest.digest(keyBytes);
            }

            System.arraycopy(keyBytes, 0, ipad, 0, keyBytes.length);
            System.arraycopy(keyBytes, 0, opad, 0, keyBytes.length);

            // XOR key with pads
            for (int i = 0; i < 64; i++) {
                ipad[i] ^= 0x36;
                opad[i] ^= 0x5c;
            }

            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(ipad);
            digest.update(text.getBytes(StandardCharsets.UTF_8));
            byte[] innerHash = digest.digest();

            digest.reset();
            digest.update(opad);
            digest.update(innerHash);
            byte[] hmacBytes = digest.digest();

            return bytesToHex(hmacBytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error computing HMAC: " + e.getMessage());
            return null;
        }
    }

    /**
     * PBKDF2 implementation
     */
    private static byte[] pbkdf2(String password, byte[] salt, int iterations, int keyLength)
            throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                keyLength
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Convert byte array to hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Constant-time comparison of byte arrays to prevent timing attacks
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

}