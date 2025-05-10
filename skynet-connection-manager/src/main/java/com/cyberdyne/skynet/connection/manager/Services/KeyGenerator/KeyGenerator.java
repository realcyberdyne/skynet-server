package com.cyberdyne.skynet.connection.manager.Services.KeyGenerator;

import java.security.SecureRandom;

public class KeyGenerator
{

    //Get generate a key string
    public static String GetGenerateKey()
    {
        // Define characters to use in the key
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder();

        // Create a secure random number generator
        SecureRandom random = new SecureRandom();

        // Generate a 24-character key
        for (int i = 0; i < 24; i++) {
            int randomIndex = random.nextInt(characters.length());
            key.append(characters.charAt(randomIndex));
        }

        return key.toString();
    }

}
