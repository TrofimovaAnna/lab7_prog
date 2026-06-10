package shared.utility;

import shared.config.LabConfig;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(LabConfig.HASH_ALGORITHM);
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException("Hash error", e); }
    }
}