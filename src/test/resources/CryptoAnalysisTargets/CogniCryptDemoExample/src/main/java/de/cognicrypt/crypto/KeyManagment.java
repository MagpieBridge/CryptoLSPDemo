
package de.cognicrypt.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/** @author CogniCrypt */
public class KeyManagment {

  public SecretKey generateSessionKey(int keySize) throws NoSuchAlgorithmException {
    KeyGenerator kg = KeyGenerator.getInstance("AES");
    kg.init(keySize);
    return kg.generateKey();
  }

  public KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(keySize);
    return kpg.generateKeyPair();
  }

}
