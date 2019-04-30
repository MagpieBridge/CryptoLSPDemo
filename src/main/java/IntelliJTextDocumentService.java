import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.MagpieTextDocumentService;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.WillSaveTextDocumentParams;

public class IntelliJTextDocumentService extends MagpieTextDocumentService {

  public IntelliJTextDocumentService(MagpieServer server) {
    super(server);
  }

  @Override
  public void willSave(WillSaveTextDocumentParams params) {
    String uri = params.getTextDocument().getUri();
    try {
      String decodedUri = URLDecoder.decode(uri, "UTF-8");
      URL url = new URI(decodedUri).toURL();
      List<Diagnostic> diagList = this.server.getDiagnostics(url);
      if (!diagList.isEmpty()) {
        PublishDiagnosticsParams pdp = new PublishDiagnosticsParams();
        pdp.setDiagnostics(diagList);
        pdp.setUri(uri);
        this.server.sendDiagnostics(pdp);
      }
    } catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
