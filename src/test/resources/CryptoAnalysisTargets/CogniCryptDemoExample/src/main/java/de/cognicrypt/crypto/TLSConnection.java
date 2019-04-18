
package de.cognicrypt.crypto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;

/** @author CogniCrypt */
public class TLSConnection {

  private SSLSocket sslSocket = null;
  private static BufferedWriter bufW = null;
  private static BufferedReader bufR = null;

  public TLSConnection(SSLSocket con) {
    sslSocket = con;
    try {
      bufW = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
      bufR = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
    } catch (IOException e) {
    }
  }

  public void closeConnection() {
    try {
      if (!sslSocket.isClosed()) {
        sslSocket.close();
      }
    } catch (IOException ex) {
      System.out.println("Could not close channel.");
      ex.printStackTrace();
    }
  }

  public boolean sendData(String content) {
    try {
      bufW.write(content + "\n");
      bufW.flush();
      return true;
    } catch (IOException ex) {
      System.out.println("Sending data failed.");
      ex.printStackTrace();
      return false;
    }
  }

  public String receiveData() {
    String readLine = null;
    try {
      readLine = bufR.readLine();
    } catch (IOException ex) {
      System.out.println("Receiving data failed.");
      ex.printStackTrace();
    }

    if (readLine == null) {
      this.closeConnection();
    }

    return readLine;
  }

  public InetAddress getSource() {
    return sslSocket.getInetAddress();
  }

  public boolean isClosed() {
    return sslSocket.isClosed();
  }

}
