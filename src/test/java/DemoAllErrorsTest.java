import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import magpiebridge.core.AnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class DemoAllErrorsTest {

  @Test
  public void test() {
    String testTargetPath = new File("src/test/resources/DemoAllErrors/src").getAbsolutePath();
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(Utils.ruleDirPath);
    Collection<AnalysisResult> results =
        analysis.analyze(Collections.singleton(testTargetPath), Collections.emptySet());
    assertTrue(results.size() == 17);
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
