import magpiebridge.core.IProjectService;
import magpiebridge.core.JavaProjectService;
import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
  public static void main(String... args) {
    String ruleDirPath = TestMain.ruleDirPath;
    // String ruleDirPath = args[0];
    MagpieServer server = new MagpieServer();
    System.out.println("server started");
    String language = "java";
    IProjectService javaProjectService = new JavaProjectService();
    server.addProjectService(language, javaProjectService);
    server.addAnalysis(language, new CryptoServerAnalysis(ruleDirPath));
    server.launchOnStdio();
  }

}
