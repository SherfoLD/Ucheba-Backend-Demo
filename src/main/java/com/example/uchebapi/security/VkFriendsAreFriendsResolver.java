package com.example.uchebapi.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VkFriendsAreFriendsResolver {

    private static final Integer FRIEND_STATUS = 3;
    private static final String clientSecret = "DELETED";

    public static boolean isSignValid(Integer inviterVkId, Integer friendVkId, String signToValidate) {
        String expectedInput = inviterVkId + "_" + friendVkId + "_" + FRIEND_STATUS + "_" + clientSecret;
        StringBuilder expectedInputSign = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(expectedInput.getBytes());

            for (byte b : messageDigest) {
                expectedInputSign.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            return false;
        }

        return expectedInputSign.toString().equals(signToValidate);
    }
}
