package de.upb.swt.insecurebank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class LoginActivity extends AppCompatActivity {

    private RSA rsa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String msg = "some msg";
        try {
            rsa = new RSA();
            byte[] siged = doSign(msg);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    private byte[] doSign(String msg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return rsa.sign(msg);
    }

}
