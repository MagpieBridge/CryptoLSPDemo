import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import magpiebridge.core.AnalysisResult;

public class TestMain {

  private static String userProject = null;

  static {
    if (System.getProperty("user.project") != null) {
      userProject = System.getProperty("user.project");
    } else {
      throw new IllegalStateException("Please specify your the project path of crypto-lsp-demo as JVM argument:\r\n"
          + "-Duser.project=<PATH_TO_crypto-lsp-demo>");
    }
    System.setProperty("log4j.configurationFile", userProject + "/src/test/resources/template-log4j2.xml");
  }

  public static String ruleDirPath = new File(userProject + "/JCA_rules").getAbsolutePath();
  public static String testTargetPath
      = new File(userProject + "/src/test/resources/CryptoAnalysisTargets").getAbsolutePath();

  public static void main(String... args) {
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(ruleDirPath);
    Collection<AnalysisResult> results = analysis.analyze(testTargetPath + File.separator + "CogniCryptDemoExample");
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}