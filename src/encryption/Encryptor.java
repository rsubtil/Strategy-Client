package encryption;

// This class is used to generate hash's of passwords and codes.

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Encryptor {
    
    // Encrypts a String with SHA256
    public static String encrypt(String input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
         
            return sb.toString();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
