import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class NeverTypeOfErrorExample {
  public static void main(String... args) throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecureRandom saltGenerator = new SecureRandom();
    byte[] salt = new byte[32];
    saltGenerator.nextBytes(salt);
    String pwd = "password";
    PBEKeySpec pbe = new PBEKeySpec(pwd.toCharArray(), salt, 20000, 128);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    SecretKeySpec ret = new SecretKeySpec(skf.generateSecret(pbe).getEncoded(), "AES");
    pbe.clearPassword();
  }
}
