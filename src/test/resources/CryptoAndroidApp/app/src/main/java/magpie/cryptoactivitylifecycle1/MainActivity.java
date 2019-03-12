package magpie.cryptoactivitylifecycle1;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;

public class MainActivity extends Activity {

    private static String URL = "http://www.google.de/search?q=";
    private Signature sig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String device = Build.DEVICE;
        URL = URL.concat(device);
        try {
            sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(getPrivateKey());
            sig.sign();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private PrivateKey getPrivateKey() {
        KeyPair gp = null;
        try {
            KeyPairGenerator kpgen = KeyPairGenerator.getInstance("RSA");
            kpgen.initialize(2048);
            gp = kpgen.generateKeyPair();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return gp.getPrivate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            connect();
        } catch (Exception ex) {
            //do nothing
        }
    }

    private void connect() throws IOException {
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
    }
}
