
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PasswordStorage {

  private byte[] salt
      = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

  private int count = 20;

  public byte[] encryptPassword(String masterpwd, String pwdClearText) throws Exception {
    Cipher pbeCipher = getCipher(Cipher.ENCRYPT_MODE , masterpwd);
    return pbeCipher.doFinal(pwdClearText.getBytes());
  }

  public byte[] decryptPassword(String masterpwd, String pwdCipherText) throws Exception {
    Cipher pbeCipher = getCipher(Cipher.DECRYPT_MODE , masterpwd);
    return pbeCipher.doFinal(pwdCipherText.getBytes());
  }
  
  private Cipher getCipher(int mode, String masterpwd) throws Exception
  {
    PBEKeySpec pbeKeySpec;
    PBEParameterSpec pbeParamSpec;
    SecretKeyFactory keyFac;

    pbeParamSpec = new PBEParameterSpec(salt, count);

    char[] pwdarr = masterpwd.toCharArray();
    pbeKeySpec = new PBEKeySpec(pwdarr,salt, count, 128);
    keyFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

    Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

    pbeCipher.init(mode, pbeKey, pbeParamSpec);

    return pbeCipher;
  }
}
