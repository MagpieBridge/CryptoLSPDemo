
package de.cognicrypt.crypto;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/** @author CogniCrypt */
public class PublicKeyEnc {

  public byte[] encrypt(SecretKey sessionKey, PublicKey publicKey) throws GeneralSecurityException {

    Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
    c.init(Cipher.WRAP_MODE, publicKey);
    byte[] sessionKeyBytes = c.wrap(sessionKey);
    return sessionKeyBytes;
  }
}
