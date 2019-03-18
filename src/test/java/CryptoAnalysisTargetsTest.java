import java.io.File;
import java.util.Collection;
import java.util.Collections;
import magpiebridge.core.AnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class CryptoAnalysisTargetsTest {

  @Test
  public void test() {
    System.setProperty("user.project", System.getProperty("user.dir"));
    String testTargetPath =
        new File(System.getProperty("user.project") + "/src/test/resources/CryptoAnalysisTargets")
            .getAbsolutePath();
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(Utils.ruleDirPath);
    Collection<AnalysisResult> results =
        analysis.analyze(
            Collections.singleton(testTargetPath + File.separator + "CogniCryptDemoExample"));
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
