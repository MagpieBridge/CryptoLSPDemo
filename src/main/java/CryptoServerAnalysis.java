import com.ibm.wala.classLoader.Module;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import magpiebridge.converter.WalaToSootIRConverter;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.ServerConfiguration;
import magpiebridge.core.ToolAnalysis;
import magpiebridge.projectservice.java.JavaProjectService;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import soot.PackManager;
import soot.Transform;
import soot.Transformer;

public class CryptoServerAnalysis implements ServerAnalysis {

  private static final Logger LOG = Logger.getLogger("main");

  private String ruleDirPath;
  private Set<String> srcPath;
  private Set<String> libPath;
  private ExecutorService exeService;
  private Future<?> last;
  private boolean webSocketOn = false;

  public CryptoServerAnalysis(String ruleDirPath) {
    this.ruleDirPath = ruleDirPath;
    exeService = Executors.newSingleThreadExecutor();
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
        if (libPath == null) {
          libPath = new HashSet<>();
          ps.getLibraryPath().stream().forEach(path -> libPath.add(path.toString()));
        }
        if (!sourcePath.isEmpty()) {
          Set<String> temp = new HashSet<>();
          sourcePath.stream().forEach(path -> temp.add(path.toString()));
          srcPath = temp;
        }
      }
    }
  }

  @Override
  public void analyze(Collection<? extends Module> files, MagpieServer server, boolean rerun) {
    if (webSocketOn) {
      setClassPath(server);
      Collection<AnalysisResult> results = Collections.emptyList();
      if (srcPath != null) {
        // do whole program analysis
        results = analyze(srcPath, libPath);
      } else {

        if (libPath == null) libPath = new HashSet<>();
        libPath.add(
            System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar");
        // only analyze relevant files
        LOG.info("Analyzing files "+ files +"\nlibPath " + libPath);
        results = analyze(files, libPath);
        LOG.info("Results: " + results);
      }
      server.consume(results, source());
    } else {
      if (last != null && !last.isDone()) {
        last.cancel(false);
        if (last.isCancelled()) LOG.info("Susscessfully cancelled last analysis and start new");
      }
      Future<?> future =
          exeService.submit(
              new Runnable() {
                @Override
                public void run() {
                  setClassPath(server);
                  Collection<AnalysisResult> results = Collections.emptyList();
                  if (srcPath != null) {
                    // do whole program analysis
                    results = analyze(srcPath, libPath);
                  } else {

                    if (libPath == null) libPath = new HashSet<>();
                    libPath.add(
                        System.getProperty("java.home")
                            + File.separator
                            + "lib"
                            + File.separator
                            + "jce.jar");
                    // only analyze relevant files
                    LOG.info("Analyzing files , libPath " + libPath);
                    results = analyze(files, libPath);
                    LOG.info("Results: " + results);
                  }
                  server.consume(results, source());
                }
              });
      last = future;
    }
  }

  public Collection<AnalysisResult> analyze(
      Collection<? extends Module> files, Set<String> libPath) {
    CryptoTransformer transformer = new CryptoTransformer(ruleDirPath, false);
    loadSourceCode(files, libPath);
    runSootPacks(transformer);
    Collection<AnalysisResult> results = transformer.getAnalysisResults();
    return results;
  }

  public Collection<AnalysisResult> analyze(Set<String> srcPath, Set<String> libPath) {
    CryptoTransformer transformer = new CryptoTransformer(ruleDirPath, false);
    WalaToSootIRConverter converter = new WalaToSootIRConverter(srcPath, libPath, null);
    converter.convert();
    runSootPacks(transformer);
    Collection<AnalysisResult> results = transformer.getAnalysisResults();
    return results;
  }

  private void loadSourceCode(Collection<? extends Module> files, Set<String> libPath) {
    WalaToSootIRConverter converter = new WalaToSootIRConverter(files, libPath);
    converter.convert();
  }

  private void runSootPacks(Transformer t) {
    PackManager.v().getPack("wjtp").add(new Transform("wjtp.cognicrypt", t));
    PackManager.v().getPack("cg").apply();
    PackManager.v().getPack("wjtp").apply();
  }

  /**
   * @return a server which runs the {@link CryptoServerAnalysis}. This is used for websocket
   *     server.
   */
  public static MagpieServer runOnServer() {
    // you need to configure JVM option for tomcat at first
    // for windows, add this line 'set JAVA_OPTS="-Duser.project=PATH\TO\crypto-lsp-demo"' to
    // tomcat\bin\catalina.bat
    // for linux, add this line 'JAVA_OPTS="-Duser.project=PATH/TO/crypto-lsp-demo"' to
    // tomcat\bin\catalina.sh
    String userProject = System.getProperty("user.project");
    String ruleDirPath = userProject + File.separator + "config" + File.separator + "JCA_rules";
    MagpieServer server = new MagpieServer(new ServerConfiguration());
    CryptoServerAnalysis c=new CryptoServerAnalysis(ruleDirPath);
    c.webSocketOn=true;
    Either<ServerAnalysis, ToolAnalysis> analysis =
        Either.forLeft(c);
    server.addAnalysis(analysis, "java");
    return server;
  }
}
