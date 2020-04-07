package dev.loupgarou.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomString {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private static final String SIMPLE_ALPHABET = "123456789abcdefghkmnpqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates random string of given length from Base65 alphabet (numbers, lowercase letters, uppercase letters).
     *
     * @param count length
     * @return random string of given length
     */
    public static String generate(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i)
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        return sb.toString();
    }
    
    /**
     */
    public static String simple(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i)
            sb.append(SIMPLE_ALPHABET.charAt(RANDOM.nextInt(SIMPLE_ALPHABET.length())));
        return sb.toString();
    }
    
    public static String toSHA1(String s) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException e) {
            return generate(s.length());
        } 
        return new String(md.digest(s.getBytes()));
    }
}