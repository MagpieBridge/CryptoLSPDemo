import javax.websocket.server.ServerEndpoint;
import magpiebridge.core.MagpieWebsocketEndpoint;

@ServerEndpoint("/websocket")
public class CryptoWebSocketServer extends MagpieWebsocketEndpoint {
  public CryptoWebSocketServer() {
    super(CryptoServerAnalysis.runOnServer());
  }
}
