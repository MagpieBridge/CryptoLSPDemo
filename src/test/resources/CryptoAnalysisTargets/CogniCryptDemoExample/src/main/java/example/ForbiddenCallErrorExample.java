import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ForbiddenCallErrorExample {
  public static void main(String... args) throws NoSuchAlgorithmException, InvalidKeySpecException {
    char[] pwd = new char[] { 'p', 'w', 'd' };
    PBEKeySpec pbe = new PBEKeySpec(pwd);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    SecretKeySpec ret = new SecretKeySpec(skf.generateSecret(pbe).getEncoded(), "AES");
    pbe.clearPassword();
  }
}
