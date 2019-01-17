import java.util.List;

import org.eclipse.lsp4j.DiagnosticSeverity;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;

import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;

public class CryptoResult implements AnalysisResult {
	private final Kind kind;
	private final Position position;
	private final String text;
	private final Iterable<Pair<Position, String>> related;
	private final DiagnosticSeverity severity;
	
	public CryptoResult(Kind kind, Position pos, String text, Iterable<Pair<Position, String>> relatedInfo,
			DiagnosticSeverity severity) {
		this.kind = kind;
		this.position = pos;
		this.text = text;
		this.related = relatedInfo;
		this.severity = severity;
	}

	public Kind kind() {
		return this.kind;
	}

	public String toString(boolean useMarkdown) {
		// TODO Auto-generated method stub
		return null;
	}

	public Position position() {
		return position;
	}

	public Iterable<Pair<Position, String>> related() {
		return related;
	}

	public DiagnosticSeverity severity() {
		return severity;
	}

	public String repair() {
		return text;
	}

}
