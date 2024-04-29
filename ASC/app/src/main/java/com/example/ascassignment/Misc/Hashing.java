package com.example.ascassignment.Misc;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Hashing {
    public static final boolean ENABLED = true;
    private static final String TAG = "Hashing";

    public static String hashPassword(String pt) { //will return hashed password
        String ret = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            ret = "";

            byte[] hashedPasswordBytes = md.digest(pt.getBytes(StandardCharsets.UTF_8));

            ret = Base64.getEncoder().encodeToString(hashedPasswordBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "hashPassword: ", e);
        }
        return ret;
    }
}
