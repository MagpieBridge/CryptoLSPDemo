

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class CryptoInsecureBankAndroidTest {
  @Test
  public void test() {
    System.setProperty("user.project", System.getProperty("user.dir"));
    String projectDir = System.getProperty("user.project");
    String androidPlatform =
        new File(projectDir + File.separator + "src/test/resources/platforms").getAbsolutePath();
    CryptoAndroidServerAnalysis analysis =
        new CryptoAndroidServerAnalysis(Utils.ruleDirPath, Utils.configPath, androidPlatform);
    String androidProjDir =
        new File(projectDir + File.separator + "src/test/resources/InsecureBank").getAbsolutePath();
    String apkFile = androidProjDir + File.separator + "app/build/outputs/apk/debug/app-debug.apk";

    // InferSourcePath infer=new InferSourcePath();
    // System.out.println(infer.sourcePath( Paths.get(androidProjDir).toAbsolutePath()));
    // Set<String> excludedPkgs= infer.getClassFullQualifiedNames();
    // System.out.println(excludedPkgs);
    // Utils.generateJar(apkFile, androidPlatform, projectDir, excludedPkgs);
    // TestMain.generateJar(apkFile, androidPlatform, androidProjDir+File.separator+"lib");

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
