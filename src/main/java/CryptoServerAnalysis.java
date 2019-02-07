import java.util.Collection;
import java.util.List;

import com.ibm.wala.classLoader.Module;

import de.upb.soot.core.SootClass;
import de.upb.soot.frontends.java.JimpleConverter;
import de.upb.soot.frontends.java.WalaClassLoader;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import soot.PackManager;
import soot.Transform;
import soot.Transformer;

public class CryptoServerAnalysis implements ServerAnalysis {

	private String ruleDirPath;

	public CryptoServerAnalysis(String ruleDirPath) {
		this.ruleDirPath = ruleDirPath;
	}

	@Override
	public String source() {
		return "CogniCrypt";
	}

	@Override
	public void analyze(Collection<Module> files, MagpieServer server) {
		// String srcPath = server.getSourceCodePath();
		// server.logger.logVerbose("analyze "+ srcPath);
		CryptoTransformer transformer = new CryptoTransformer(ruleDirPath);
		loadSourceCode(files);
		runSootPacks(transformer);
		Collection<AnalysisResult> results = transformer.getAnalysisResults();
		server.consume(results, source());
	}

	private void loadSourceCode(Collection<? extends Module> files) {
		// use WALA source-code front end to load classes
		WalaClassLoader loader = new WalaClassLoader(files);
		List<SootClass> sootClasses = loader.getSootClasses();
		JimpleConverter jimpleConverter = new JimpleConverter(sootClasses);
		jimpleConverter.convertAllClasses();
	}

	private void loadSourceCode(String srcPath) {
		WalaClassLoader loader = new WalaClassLoader(srcPath);
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
