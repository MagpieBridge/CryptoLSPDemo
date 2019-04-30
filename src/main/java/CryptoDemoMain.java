import java.io.File;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.projectservice.java.AndroidProjectService;
import magpiebridge.projectservice.java.JavaProjectService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CryptoDemoMain {
  public static void main(String... args) throws ParseException {
    Options options = new Options();
    options.addOption("h", "help", false, "Print this mesage");
    options.addOption("a", "android", false, "Analyze android project");
    options.addOption("p", "platform", true, "The location of android platform");
    options.addOption("c", "config", true, "The location of JCA rules and configuration files");
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    HelpFormatter helper = new HelpFormatter();
    String cmdLineSyntax =
        "\nJava Project:-c <JCA rules and configuration files>\nAndroid Project: -a -p <android platform> -c <JCA rules and config files>\n";
    if (cmd.hasOption('h')) {
      helper.printHelp(cmdLineSyntax, options);
      return;
    }
    boolean android = false;
    if (cmd.hasOption("a")) {
      android = true;
      if (!cmd.hasOption("p")) {
        helper.printHelp(cmdLineSyntax, options);
      }
    }
    String config = null;
    if (!cmd.hasOption("c")) {
      helper.printHelp(cmdLineSyntax, options);
    } else config = cmd.getOptionValue("c");
    String ruleDirPath = new File(config + "/JCA_rules").getAbsolutePath();
    String flowdroidConfigPath = new File(config).getAbsolutePath();
    MagpieServer server = new MagpieServer();
    String language = "java";
    if (!android) {
      IProjectService javaProjectService = new JavaProjectService();
      server.addProjectService(language, javaProjectService);
      server.addAnalysis(language, new CryptoServerAnalysis(ruleDirPath));
    } else {
      IProjectService androidProjectService = new AndroidProjectService();
      server.addProjectService(language, androidProjectService);
      String androidPlatform = cmd.getOptionValue("p");
      server.addAnalysis(
          language,
          new CryptoAndroidServerAnalysis(ruleDirPath, flowdroidConfigPath, androidPlatform));
    }
    server.launchOnStdio();
  }
}
