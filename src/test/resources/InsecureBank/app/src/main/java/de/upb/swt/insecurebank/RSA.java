package de.upb.swt.insecurebank;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class RSA {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSA() throws NoSuchAlgorithmException {
        KeyPairGenerator kpgen = KeyPairGenerator.getInstance("RSA");
        kpgen.initialize(512);
        KeyPair keyPair = kpgen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public byte[] sign(String message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature instance = Signature.getInstance("SHA256withRSA");
        byte[] bytes = message.getBytes();
        instance.update(bytes);
        instance.sign();
        return bytes;
    }

    public boolean verify(String message, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature instance = Signature.getInstance("SHA256withRSA");
        instance.initVerify(publicKey);
        instance.update(message.getBytes());
        boolean isVerfied = instance.verify(signature);
        return isVerfied;
    }

}

