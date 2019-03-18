import java.io.File;
import java.util.Collection;
import java.util.Collections;
import magpiebridge.core.AnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;

public class StandardJCATask3Test {

  @Ignore
  public void test() {
    System.setProperty("user.project", System.getProperty("user.dir"));
    String testTargetPath =
        new File(System.getProperty("user.project") + "/src/test/resources/StandardJCATasks")
            .getAbsolutePath();
    CryptoServerAnalysis analysis = new CryptoServerAnalysis(Utils.ruleDirPath);
    Collection<AnalysisResult> results =
        analysis.analyze(Collections.singleton(testTargetPath + File.separator + "Task3"));
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
