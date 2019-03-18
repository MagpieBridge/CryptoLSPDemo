import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class CryptoAndroidServerAnalysisTest {
  @Test
  public void test() {
    System.setProperty("user.project", System.getProperty("user.dir"));
    String projectDir = System.getProperty("user.project");
    String androidPlatform =
        new File(projectDir + File.separator + "src\\test\\resources\\platforms").getAbsolutePath();
    CryptoAndroidServerAnalysis analysis =
        new CryptoAndroidServerAnalysis(Utils.ruleDirPath, Utils.configPath, androidPlatform);
    String apkFile =
        projectDir
            + File.separator
            + "src\\test\\resources\\CryptoAndroidApp\\app\\build\\outputs\\apk\\debug\\app-debug.apk";
    Set<String> srcPath = new HashSet<String>();
    Set<String> libPath = new HashSet<String>();
    srcPath.add(
        projectDir
            + File.separator
            + "src\\test\\resources\\CryptoAndroidApp\\app\\build\\generated\\source");
    srcPath.add(projectDir + File.separator + "src\\test\\resources\\CryptoAndroidApp\\app\\src");
    Set<String> libDir = new HashSet<>();
    libDir.add(projectDir + File.separator + "src\\test\\resources\\CryptoAndroidApp\\app\\libs");
    for (String dir : libDir) {
      for (File file : new File(dir).listFiles()) {
        if (file.getName().endsWith(".jar")) {
          libPath.add(file.getAbsolutePath());
        }
      }
    }
    Collection<AnalysisResult> results =
        analysis.analyze(androidPlatform, apkFile, srcPath, libPath);
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
