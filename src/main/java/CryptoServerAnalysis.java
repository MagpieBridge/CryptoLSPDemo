import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.ibm.wala.classLoader.Module;

import de.upb.soot.core.SootClass;
import de.upb.soot.frontends.java.JimpleConverter;
import de.upb.soot.frontends.java.WalaClassLoader;
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

	public String source() {
		return "CogniCrypt";
	}

	public void analyze(Collection<Module> files, MagpieServer server) {
		CryptoTransformer transformer = new CryptoTransformer(ruleDirPath);
		loadSourceCode(server.getSourceCodePath());
		runSootPacks(transformer);
	}

	private void loadSourceCode(String sourceCodePath) {
		// use WALA source-code front end to load classes
		WalaClassLoader loader = new WalaClassLoader(sourceCodePath);
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
