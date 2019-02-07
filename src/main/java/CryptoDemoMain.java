import magpiebridge.core.MagpieServer;

public class CryptoDemoMain {
	public static void main(String... args) {
		String ruleDirPath = "E:/Git/Github/crypto-lsp-demo/src/test/resources";
		//String ruleDirPath = args[0];
		MagpieServer server = new MagpieServer();
		server.addAnalysis("java", new CryptoServerAnalysis(ruleDirPath));
		server.launchOnStdio();
	}

}
