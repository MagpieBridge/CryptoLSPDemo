import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import boomerang.preanalysis.BoomerangPretransformer;
import crypto.Utils;
import crypto.HeadlessCryptoScanner.CG;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CryptoScanner;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import magpiebridge.core.AnalysisResult;
import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

public class CryptoTransformer extends SceneTransformer {
  private String ruleDir;
  private CrytoErrorReporter errorReporter;

  public CryptoTransformer(String ruleDir) {
    this.ruleDir = ruleDir;
    initilizeSootOptions();
    this.errorReporter = new CrytoErrorReporter();
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    BoomerangPretransformer.v().apply();
    final JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);
    List<CryptSLRule> rules = getRules();
    final CrySLResultsReporter reporter = new CrySLResultsReporter();
    CryptoScanner scanner = new CryptoScanner() {

      @Override
      public boolean rulesInSrcFormat() {
        return true;
      }

      @Override
      public boolean isCommandLineMode() {
        return true;
      }

      @Override
      public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
        return icfg;
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
      System.out
          .println("CogniCrypt did not find any rules to start the analysis for. \n It checked for rules in " + ruleDir);
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
    Options.v().set_keep_line_number(true);
    Options.v().set_prepend_classpath(true);// append rt.jar to soot class path
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