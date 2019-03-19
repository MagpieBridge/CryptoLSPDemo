import java.io.File;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import magpiebridge.core.MagpieServer;
import org.eclipse.lsp4j.launch.websockets.LSPWebSocketServer;

@ServerEndpoint("/websocket")
public class CryptoWebSocketServer extends LSPWebSocketServer<MagpieServer> {

  public CryptoWebSocketServer() {
    super(
        () -> {
          // you need to configure JVM option for tomcat at first
          // for windows, add this line 'set JAVA_OPTS="-Duser.project=PATH\TO\crypto-lsp-demo"' to
          // tomcat\bin\catalina.bat
          // for linux, add this line 'JAVA_OPTS="-Duser.project=PATH/TO/crypto-lsp-demo"' to
          // tomcat\bin\catalina.sh
          String userProject = System.getProperty("user.project");
          String ruleDirPath = userProject + File.separator + Utils.ruleDirPath;
          MagpieServer server = new MagpieServer();
          server.addAnalysis("java", new CryptoServerAnalysis(ruleDirPath));
          return server;
        },
        MagpieServer.class);
  }

  @Override
  @OnError
  public void onError(Throwable e, Session session) {
    e.printStackTrace();
  }
}