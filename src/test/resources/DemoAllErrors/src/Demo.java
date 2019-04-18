  import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Demo {
  public static void main(String... args) {
    Demo demo = new Demo();
    try {
      demo.showConstraintError();
      demo.showForbiddenMethodError();
      demo.showIncompleteOperationError();
      demo.showNeverTypeOfError();
      demo.showRequiredPredicateError();
      demo.showTypeStateError();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  public void showConstraintError() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
      IllegalBlockSizeException, BadPaddingException {
    String data = "some data";
    String key = "secret key";
    /**
    CogniCrypt reports the parameter passed in the following line is insecure.
    **/
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    cipher.doFinal(data.getBytes());
  }

  public void showTypeStateError() throws GeneralSecurityException {
    String data = "some data";
    Signature s = Signature.getInstance("SHA256withRSA");
    s.initSign(getPrivateKey());
    /**
     * The Signature API expects a call to update here. This call supplied the actual data to the signature instance. A call
     * such as s.update(data); would resolve this finding.
     */
    // s.update(data.getBytes());
    s.sign();
  }

  public void showIncompleteOperationError()
      throws NoSuchAlgorithmException, NoSuchPaddingException, GeneralSecurityException {
    String data = "some data";
    Signature instance = Signature.getInstance("SHA256withRSA");
    instance.initSign(getPrivateKey());
    instance.update(data.getBytes());
    /**
     * The following call is missing, therefore the Signature object is never actually used to compute a Signature.
     */
    // instance.sign();
  }

  public void showRequiredPredicateError() throws GeneralSecurityException {
    String data = "some data";
    KeyGenerator keygen = KeyGenerator.getInstance("AES");

    /**
     * CogniCrypt reports an error in the next line saying that the key size is chosen inappropriately.
     */
    keygen.init(46);
    SecretKey key = keygen.generateKey();
    Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");

    /**
     * CogniCrypt reports an error in the next line as the key flowing to this Cipher usage was not generated securely.
     */
    c.init(Cipher.ENCRYPT_MODE, key);
    byte[] encText = c.doFinal(data.getBytes());
  }

  public void showNeverTypeOfError() throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecureRandom saltGenerator = new SecureRandom();
    byte[] salt = new byte[32];
    saltGenerator.nextBytes(salt);
    String pwd = "password";
    PBEKeySpec pbe = new PBEKeySpec(pwd.toCharArray(), salt, 20000, 128);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    SecretKeySpec ret = new SecretKeySpec(skf.generateSecret(pbe).getEncoded(), "AES");
    pbe.clearPassword();
  }

  public void showForbiddenMethodError() throws NoSuchAlgorithmException, InvalidKeySpecException {
    char[] pwd = new char[] { 'p', 'w', 'd' };
    PBEKeySpec pbe = new PBEKeySpec(pwd);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    SecretKeySpec ret = new SecretKeySpec(skf.generateSecret(pbe).getEncoded(), "AES");
    pbe.clearPassword();
  }

  private PrivateKey getPrivateKey() throws GeneralSecurityException {
    KeyPairGenerator kpgen = KeyPairGenerator.getInstance("RSA");
    kpgen.initialize(2048);
    KeyPair gp = kpgen.generateKeyPair();
    return gp.getPrivate();
  }

}
