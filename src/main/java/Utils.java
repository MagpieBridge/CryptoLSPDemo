import java.io.File;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import soot.Main;

public class Utils {

  private static String userProject = null;

  static {
    if (System.getProperty("user.project") != null) {
      userProject = System.getProperty("user.project");
    } else {
      throw new IllegalStateException(
          "Please specify your the project path of crypto-lsp-demo as JVM argument:\r\n"
              + "-Duser.project=<PATH_TO_crypto-lsp-demo>");
    }
    System.setProperty(
        "log4j.configurationFile", userProject + "/src/test/resources/template-log4j2.xml");
  }

  public static String ruleDirPath = new File(userProject + "/JCA_rules").getAbsolutePath();
  public static String configPath = new File(userProject + "/config").getAbsolutePath();

  public static void generateJar(
      String apk, String androidPlatform, String outputDir, Set<String> excludedClasses) {
    String[] args = {
      "-process-dir",
      apk,
      "-src-prec",
      "apk",
      "-android-jars",
      androidPlatform,
      "-output-format",
      "class",
      "-allow-phantom-refs",
      "-output-jar",
      "-output-dir",
      outputDir
    };
    String[] exclude = new String[excludedClasses.size() * 2];
    int i = 0;
    for (String pkg : excludedClasses) {
      exclude[i] = "-exclude";
      exclude[i + 1] = pkg;
      i = i + 2;
    }
    String[] ops = (String[]) ArrayUtils.addAll(args, exclude);
    Main.main(ops);
  }
}
