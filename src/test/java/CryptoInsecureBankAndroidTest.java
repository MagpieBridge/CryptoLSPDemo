import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import magpiebridge.projectservice.java.InferSourcePath;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class CryptoInsecureBankAndroidTest {
  @Test
  public void test() {
    String androidPlatform = new File("src/test/resources/platforms").getAbsolutePath();
    CryptoAndroidServerAnalysis analysis =
        new CryptoAndroidServerAnalysis(Utils.ruleDirPath, Utils.configPath, androidPlatform);
    String androidProjDir = new File("src/test/resources/InsecureBank").getAbsolutePath();
    String apkFile = androidProjDir + File.separator + "app/build/outputs/apk/debug/app-debug.apk";

    InferSourcePath infer = new InferSourcePath();
    infer.sourcePath(Paths.get("src/test/resources/InsecureBank/"));
    Set<String> excludedPkgs = infer.getClassFullQualifiedNames();
    Utils.generateJar(apkFile, androidPlatform, "src/test/resources/InsecureBank/", excludedPkgs);

    Set<String> srcPath = new HashSet<String>();
    Set<String> libPath = new HashSet<String>();
    srcPath.add("src/test/resources/InsecureBank/app/src/main/java");
    // libPath.add(new
    // File(androidPlatform+File.separator+"android-28/android.jar").getAbsolutePath());
    libPath.add("src/test/resources/InsecureBank/out.jar");
    Collection<AnalysisResult> results =
        analysis.analyze(androidPlatform, apkFile, srcPath, libPath);
    for (AnalysisResult re : results) {
      LogManager.getLogger().error(re.toString());
    }
  }
}
