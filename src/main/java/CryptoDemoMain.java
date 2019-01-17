import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
	public static void main(String... args) {
		String ruleDirPath = args[0];
		MagpieServer server = new MagpieServer();
		server.addAnalysis("java", new CryptoServerAnalysis(ruleDirPath));
		server.launchOnStdio();
	}

}
