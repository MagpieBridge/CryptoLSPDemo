import java.util.List;

import org.eclipse.lsp4j.DiagnosticSeverity;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;

import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;

public class CryptoResult implements AnalysisResult {
	private final Kind kind;
	private final Position position;
	private final String message;
	private final Iterable<Pair<Position, String>> related;
	private final DiagnosticSeverity severity;
	private final String repair;

	public CryptoResult(Kind kind, Position pos, String msg, Iterable<Pair<Position, String>> relatedInfo,
			DiagnosticSeverity severity, String repair) {
		this.kind = kind;
		this.position = pos;
		this.message = msg;
		this.related = relatedInfo;
		this.severity = severity;
		this.repair = repair;
	}

	public Kind kind() {
		return this.kind;
	}

	public String toString(boolean useMarkdown) {
		return message;
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
		return repair;
	}

}
