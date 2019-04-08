import com.ibm.wala.classLoader.Module;
import de.upb.soot.core.SootClass;
import de.upb.soot.frontends.java.JimpleConverter;
import de.upb.soot.frontends.java.WalaClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.JavaProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import soot.PackManager;
import soot.Transform;
import soot.Transformer;

public class CryptoServerAnalysis implements ServerAnalysis {

  private static final Logger LOG = Logger.getLogger("main");

  private String ruleDirPath;
  private Set<String> srcPath;
  private Set<String> libPath;

  public CryptoServerAnalysis(String ruleDirPath) {
    this.ruleDirPath = ruleDirPath;
  }

  @Override
  public String source() {
    return "CogniCrypt";
  }

  /**
   * set up source code path and library path with the project service provided by the server.
   *
   * @param server
   */
  public void setClassPath(MagpieServer server) {
    if (srcPath == null) {
      Optional<IProjectService> opt = server.getProjectService("java");
      if (opt.isPresent()) {
        JavaProjectService ps = (JavaProjectService) server.getProjectService("java").get();
        Set<Path> sourcePath = ps.getSourcePath();
        if (libPath == null)
          ps.getLibraryPath().stream().forEach(path -> libPath.add(path.toString()));
        if (!sourcePath.isEmpty()) {
          Set<String> temp = new HashSet<>();
          sourcePath.stream().forEach(path -> temp.add(path.toString()));
          srcPath = temp;
        }
      }
    }
  }

  @Override
  public void analyze(Collection<Module> files, MagpieServer server) {
    setClassPath(server);
    Collection<AnalysisResult> results = Collections.emptyList();
    if (srcPath != null) {
      // do whole program analysis
      results = analyze(srcPath, libPath);
    } else {
      // only analyze relevant files
      results = analyze(files);
    }
    server.consume(results, source());
  }

  public Collection<AnalysisResult> analyze(Collection<? extends Module> files) {
    CryptoTransformer transformer = new CryptoTransformer(ruleDirPath, false);
    loadSourceCode(files);
    runSootPacks(transformer);
    Collection<AnalysisResult> results = transformer.getAnalysisResults();
    return results;
  }

  public Collection<AnalysisResult> analyze(Set<String> srcPath, Set<String> libPath) {
    CryptoTransformer transformer = new CryptoTransformer(ruleDirPath, false);
    WalaClassLoader loader = new WalaClassLoader(srcPath, libPath, null);
    List<SootClass> sootClasses = loader.getSootClasses();
    JimpleConverter jimpleConverter = new JimpleConverter(sootClasses);
    jimpleConverter.convertAllClasses();
    runSootPacks(transformer);
    Collection<AnalysisResult> results = transformer.getAnalysisResults();
    return results;
  }

  private void loadSourceCode(Collection<? extends Module> files) {
    // use WALA source-code front end to load classes
    WalaClassLoader loader = new WalaClassLoader(files);
    List<SootClass> sootClasses = loader.getSootClasses();
    JimpleConverter jimpleConverter = new JimpleConverter(sootClasses);
    jimpleConverter.convertAllClasses();
  }

  private void runSootPacks(Transformer t) {
    PackManager.v().getPack("wjtp").add(new Transform("wjtp.cognicrypt", t));
    PackManager.v().getPack("cg").apply();
    PackManager.v().getPack("wjtp").apply();
  }
}
