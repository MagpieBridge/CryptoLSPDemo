import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import magpiebridge.core.AnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;

public class StandardJCATask1Test {

  @Ignore
  public void test() {
    String testTargetPath = new File("src/test/resources/StandardJCATasks").getAbsolutePath();
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(Utils.ruleDirPath);
    Collection<AnalysisResult> results =
        analysis.analyze(
            Collections.singleton(testTargetPath + File.separator + "Task1"),
            Collections.emptySet());
    assertTrue(results.size() == 3);
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
