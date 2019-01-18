import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
	public static void main(String... args) {
		String ruleDirPath = "src/test/resources";
		MagpieServer server = new MagpieServer();
		server.addAnalysis("java", new CryptoServerAnalysis(ruleDirPath));
		server.launchOnStdio();
	}

}
