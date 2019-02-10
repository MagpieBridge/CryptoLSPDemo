import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
	public static void main(String... args) {
		String ruleDirPath = "/Users/dolby/git/CryptoLSPDemo/src/test/resources";
		//String ruleDirPath = args[0];
		MagpieServer server = new MagpieServer();
		server.addAnalysis("java", new CryptoServerAnalysis(ruleDirPath));
		server.launchOnStdio();
	}

}
