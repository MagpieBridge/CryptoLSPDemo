
import magpiebridge.core.IProjectService;
import magpiebridge.core.JavaProjectService;
import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
  public static void main(String... args) {
    boolean android = false;
    String ruleDirPath = Utils.ruleDirPath;
    // String ruleDirPath = args[0];
    MagpieServer server = new MagpieServer();
    System.out.println("server started");
    String language = "java";
    IProjectService javaProjectService = new JavaProjectService();
    server.addProjectService(language, javaProjectService);
    if (!android) server.addAnalysis(language, new CryptoServerAnalysis(ruleDirPath));
    else
      server.addAnalysis(
          language,
          new CryptoAndroidServerAnalysis(
              ruleDirPath, Utils.configPath, "E:\\Git\\androidPlatforms"));
    server.launchOnStdio();
  }
}
