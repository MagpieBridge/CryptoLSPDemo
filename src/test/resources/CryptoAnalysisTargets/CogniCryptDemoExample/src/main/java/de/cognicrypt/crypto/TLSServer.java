
package de.cognicrypt.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/** @author CogniCrypt */
public class TLSServer {
  private static SSLServerSocket sslServersocket = null;

  public TLSServer(int port) {

    InputStream input = null;
    String pwd = null;

    try {
      // If you move the generated code in another package (default of CogniCrypt is Crypto),
      // you need to change the parameter (replacing Crypto with the package name).
      input = Object.class.getClass().getResourceAsStream("/Crypto/serverConfig.properties");
      Properties prop = new Properties();
      prop.load(input);
      pwd = prop.getProperty("pwd");

    } catch (IOException ex) {
      System.err.println("Could not read keystore properties from config.");
      ex.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    System.setProperty("javax.net.ssl.keyStorePassword", pwd);

    SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    try {
      sslServersocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

      setCipherSuites();
      setProtocols();
    } catch (IOException ex) {
      System.out.println(
          "Connection to server could not be established. Please check whether the ip/hostname and port are correct");
      ex.printStackTrace();
    }
  }

  public void startAcceptingConnections() {
    System.out.println("Accepting connections now.");
    while (true) {
      try {
        Socket incoming = sslServersocket.accept();
        Thread newConnectionHandler = new Thread() {

          @Override
          public void run() {
            TLSConnection tlsConnection = new TLSConnection((SSLSocket) incoming);
            System.out.println("Accepted connection from " + tlsConnection.getSource().getHostAddress() + ".");
            tlsConnection.receiveData();
          }
        };

        newConnectionHandler.start();
      } catch (IOException e) {
        System.err.println("Establishing connection with client failed.");
      }
    }
  }

  private void setCipherSuites() {
    if (sslServersocket != null) {
      // Insert cipher suites here
      sslServersocket.setEnabledCipherSuites(
          new String[] { "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
              "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
              "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
              "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"

          });
    }
  }

  private void setProtocols() {
    if (sslServersocket != null) {
      // Insert TLSxx here
      sslServersocket.setEnabledProtocols(new String[] { "TLSv1.2", "TLSv1.1", "TLSv1" });
    }
  }
}
