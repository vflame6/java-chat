package chatServer;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
    private PasswordHash() {}

    public static String getPasswordHash(String password) {
        byte[] passwordBytes = new byte[0];
        try {
            passwordBytes = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = md.digest(passwordBytes);
        BigInteger no = new BigInteger(1, digest);

        String passwordHash = no.toString(16);
        while (passwordHash.length() < 32) {
            passwordHash = "0" + passwordHash;
        }
        return passwordHash;
    }
}
