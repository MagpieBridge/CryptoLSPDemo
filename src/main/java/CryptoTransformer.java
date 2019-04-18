import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.preanalysis.BoomerangPretransformer;
import com.google.common.collect.Lists;
import crypto.HeadlessCryptoScanner.CG;
import crypto.Utils;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CryptoScanner;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import magpiebridge.core.AnalysisResult;
import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.options.Options;

public class CryptoTransformer extends SceneTransformer {
  private String ruleDir;
  private CryptoErrorReporter errorReporter;
  private boolean isAndroid;

  public CryptoTransformer(String ruleDir, boolean isAndroid) {
    this.ruleDir = ruleDir;
    this.isAndroid = isAndroid;
    if (!isAndroid) initilizeSootOptions();
    this.errorReporter = new CryptoErrorReporter();
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    BoomerangPretransformer.v().reset();
    BoomerangPretransformer.v().apply();
    //  final JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);
    ObservableICFG<Unit, SootMethod> icfg = new ObservableDynamicICFG(true);
    List<CryptSLRule> rules = getRules();
    final CrySLResultsReporter reporter = new CrySLResultsReporter();
    CryptoScanner scanner =
        new CryptoScanner() {
          @Override
          public ObservableICFG<Unit, SootMethod> icfg() {
            return icfg;
          }

          @Override
          public CrySLResultsReporter getAnalysisListener() {
            return reporter;
          }

          @Override
          public boolean isCommandLineMode() {
            return true;
          }

          @Override
          public boolean rulesInSrcFormat() {
            return false;
          }
        };
    reporter.addReportListener(errorReporter);
    scanner.scan(rules);
  }

  private List<CryptSLRule> getRules() {
    List<CryptSLRule> rules = Lists.newArrayList();
    File[] listFiles = new File(ruleDir).listFiles();
    for (File file : listFiles) {
      if (file != null && file.getName().endsWith(".cryptslbin")) {
        rules.add(CryptSLRuleReader.readFromFile(file));
      }
    }
    if (rules.isEmpty()) {
      System.out.println(
          "CogniCrypt did not find any rules to start the analysis for. \n It checked for rules in "
              + ruleDir);
    }
    return rules;
  }

  private List<String> getExcludeList() {
    List<String> exList = new LinkedList<String>();
    List<CryptSLRule> rules = getRules();
    for (CryptSLRule r : rules) {
      exList.add(Utils.getFullyQualifiedName(r));
    }
    return exList;
  }

  private List<String> getIncludeList() {
    List<String> includeList = new LinkedList<String>();
    return includeList;
  }

  private void initilizeSootOptions() {
    // set up soot options
    G.v().reset();
    Options.v().set_whole_program(true);
    switch (callGraphAlogrithm()) {
      case CHA:
        Options.v().setPhaseOption("cg.cha", "on");
        Options.v().setPhaseOption("cg", "all-reachable:true");
        break;
      case SPARK_LIBRARY:
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg", "all-reachable:true,library:any-subtype");
        break;
      case SPARK:
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg", "all-reachable:true");
        break;
      default:
        throw new RuntimeException("No call graph option selected!");
    }
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);
    // Options.v().set_keep_line_number(true);
    Options.v().set_prepend_classpath(true); // append rt.jar to soot class path
    Options.v().set_soot_classpath(File.pathSeparator + pathToJCE());
    Options.v().set_include(getIncludeList());
    Options.v().set_exclude(getExcludeList());
    Options.v().set_full_resolver(true);
    Scene.v().loadNecessaryClasses();
  }

  private CG callGraphAlogrithm() {
    return CG.CHA;
  }

  private static String pathToJCE() {
    // When whole program mode is disabled, the classpath misses jce.jar
    return System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
  }

  public Collection<AnalysisResult> getAnalysisResults() {
    return errorReporter.results;
  }
}
