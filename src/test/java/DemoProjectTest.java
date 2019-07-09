import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import magpiebridge.projectservice.java.JavaProjectService;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class DemoProjectTest {
  @Test
  public void test() {
    String testTargetPath = new File("src/test/resources/DemoProject").getAbsolutePath();
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(Utils.ruleDirPath);
    Collection<AnalysisResult> results =
        analysis.analyze(Collections.singleton(testTargetPath), Collections.emptySet());
    assertTrue(results.size() == 3);
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
