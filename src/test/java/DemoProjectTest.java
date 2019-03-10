import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;

import magpiebridge.core.AnalysisResult;

public class DemoProjectTest {
  @Test
  public void test() {
    System.setProperty("user.project", System.getProperty("user.dir"));
    String testTargetPath
        = new File(System.getProperty("user.project") + "/src/test/resources/DemoProject").getAbsolutePath();
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(TestMain.ruleDirPath);
    Collection<AnalysisResult> results = analysis.analyze(Collections.singleton(testTargetPath));
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
