package cyberdyne.generator.Functions;

import java.security.SecureRandom;

public class RandomLoginPassword
{

    public static String Password = "";

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARS = LOWER + UPPER + DIGITS;
    private static final SecureRandom random = new SecureRandom();


    public RandomLoginPassword()
    {
        Password = generateRandomPassword(8);
        System.out.println("Password is : "+Password);
    }

    public static String generateRandomPassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each set
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));

        // Fill the rest with random characters
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle characters for better randomness
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            // Swap
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

}
