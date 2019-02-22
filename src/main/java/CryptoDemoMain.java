import magpiebridge.core.IProjectService;
import magpiebridge.core.JavaProjectService;
import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
	public static void main(String... args) {
		String ruleDirPath = "/Users/dolby/git/CryptoLSPDemo/src/test/resources";
		// String ruleDirPath = args[0];
		System.out.println("server started");
		MagpieServer server = new MagpieServer();
		String language = "java";
		IProjectService javaProjectService = new JavaProjectService();
		server.addProjectService(language, javaProjectService);
		server.addAnalysis(language, new CryptoServerAnalysis(ruleDirPath));
		server.launchOnStdio();
	}

}
