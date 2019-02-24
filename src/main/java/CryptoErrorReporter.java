import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;

import de.upb.soot.frontends.java.PositionTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.lsp4j.DiagnosticSeverity;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import crypto.analysis.errors.AbstractError;
import crypto.reporting.ErrorMarkerListener;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;

public class CryptoErrorReporter extends ErrorMarkerListener {
  protected Collection<AnalysisResult> results;

  public CryptoErrorReporter() {
    results = new ArrayList<AnalysisResult>();
  }

  @Override
  public void afterAnalysis() {
    for (SootClass klass : this.errorMarkers.rowKeySet()) {
      for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(klass).entrySet()) {
        for (AbstractError error : e.getValue()) {
          Unit stmt = error.getErrorLocation().getUnit().get();
          Position position = ((PositionTag) stmt.getTag("PositionTag")).getPosition();
          String msg = String.format("%s violating CrySL rule for %s. %s", error.getClass().getSimpleName(),
              error.getRule().getClassName(), error.toErrorMarkerString());
          // TODO. get relatedInfo from crypto analysis.
          List<Pair<Position, String>> relatedInfo = Collections.emptyList();
          CryptoResult res = new CryptoResult(Kind.Diagnostic, position, msg, relatedInfo, DiagnosticSeverity.Error, "");
          results.add(res);
        }
      }
    }
  }

}