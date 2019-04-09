import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ErrorWithObjectAllocation;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.reporting.ErrorMarkerListener;
import crypto.rules.CryptSLSplitter;
import crypto.rules.CryptSLValueConstraint;
import de.upb.soot.frontends.java.PositionInfoTag;
import de.upb.soot.jimple.basic.PositionInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import org.eclipse.lsp4j.DiagnosticSeverity;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractInvokeExpr;

public class CryptoErrorReporter extends ErrorMarkerListener {
  protected Collection<AnalysisResult> results;

  public CryptoErrorReporter() {
    results = new ArrayList<AnalysisResult>();
  }

  public Pair<Position, String> getRepair(AbstractError error, PositionInfo positionInfo) {
    StringBuilder replace = new StringBuilder();
    Position pos = null;
    if (error instanceof ConstraintError) {
      ConstraintError cError = (ConstraintError) error;
      if (cError.getBrokenConstraint() instanceof CryptSLValueConstraint) {
        CryptSLValueConstraint brokenConstraint =
            (CryptSLValueConstraint) cError.getBrokenConstraint();
        CryptSLSplitter splitter = brokenConstraint.getVar().getSplitter();
        if (splitter != null) {
          Stmt stmt = cError.getCallSiteWithExtractedValue().getVal().stmt().getUnit().get();
          String[] splitValues = new String[] {""};
          if (stmt instanceof AssignStmt) {
            Value rightSide = ((AssignStmt) stmt).getRightOp();
            if (rightSide instanceof Constant) {
              splitValues = filterQuotes(rightSide.toString()).split(splitter.getSplitter());
            } else if (rightSide instanceof AbstractInvokeExpr) {
              List<Value> args = ((AbstractInvokeExpr) rightSide).getArgs();
              for (Value arg : args) {
                if (arg.getType()
                    .toQuotedString()
                    .equals(brokenConstraint.getVar().getJavaType())) {
                  splitValues = filterQuotes(arg.toString()).split(splitter.getSplitter());
                  break;
                }
              }
            }
          } else {
            splitValues =
                filterQuotes(stmt.getInvokeExpr().getUseBoxes().get(0).getValue().toString())
                    .split(splitter.getSplitter());
          }
          if (splitValues.length >= splitter.getIndex()) {
            for (int i = 0; i < splitter.getIndex(); i++) {
              replace.append(splitValues[i]);
              replace.append(splitter.getSplitter());
            }
          }
        }
        List<String> repairs = brokenConstraint.getValueRange();
        if (!repairs.isEmpty()) {
          replace.append(repairs.get(0));
        }
        int paramIndex = cError.getCallSiteWithExtractedValue().getCallSite().getIndex();
        pos = positionInfo.getOperandPosition(paramIndex);
      }
    }
    if (error instanceof IncompleteOperationError) {
      IncompleteOperationError iError = (IncompleteOperationError) error;
    }
    if (error instanceof TypestateError) {
      TypestateError tError = (TypestateError) error;
    }
    if (error instanceof ForbiddenMethodError) {
      ForbiddenMethodError fError = (ForbiddenMethodError) error;
    }
    if (error instanceof ImpreciseValueExtractionError) {
      ImpreciseValueExtractionError ivError = (ImpreciseValueExtractionError) error;
    }
    if (error instanceof PredicateContradictionError) {
      PredicateContradictionError pError = (PredicateContradictionError) error;
    }
    if (error instanceof RequiredPredicateError) {
      RequiredPredicateError rError = (RequiredPredicateError) error;
    }
    return Pair.make(pos, replace.toString());
  }

  public List<Pair<Position, String>> getRelated(AbstractError error) {
    List<Pair<Position, String>> related = new ArrayList<>();
    if (error instanceof ErrorWithObjectAllocation) {
      ErrorWithObjectAllocation err = (ErrorWithObjectAllocation) error;
      for (sync.pds.solver.nodes.Node<Statement, Val> node : err.getDataFlowPath()) {
        if (node.stmt().getUnit().isPresent()) {
          PositionInfoTag tag =
              (PositionInfoTag) node.stmt().getUnit().get().getTag("PositionInfoTag");
          if (tag != null) {
            // just add stmt positions on the data flow path to related for now
            Position stmtPos = tag.getPositionInfo().getStmtPosition();
            related.add(Pair.make(stmtPos, ""));
          }
        }
      }
    }
    return related;
  }

  @Override
  public void afterAnalysis() {
    for (SootClass klass : this.errorMarkers.rowKeySet()) {
      for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(klass).entrySet()) {
        for (AbstractError error : e.getValue()) {
          Unit stmt = error.getErrorLocation().getUnit().get();
          PositionInfo positionInfo =
              ((PositionInfoTag) stmt.getTag("PositionInfoTag")).getPositionInfo();
          String msg =
              String.format(
                  "%s violating CrySL rule for %s. %s",
                  error.getClass().getSimpleName(),
                  error.getRule().getClassName(),
                  error.toErrorMarkerString());
          // TODO. get relatedInfo from crypto analysis.
          List<Pair<Position, String>> relatedInfo = getRelated(error);
          Pair<Position, String> repair = getRepair(error, positionInfo);
          CryptoResult res =
              new CryptoResult(
                  Kind.Diagnostic,
                  positionInfo.getStmtPosition(),
                  msg,
                  relatedInfo,
                  DiagnosticSeverity.Error,
                  repair);
          results.add(res);
        }
      }
    }
  }
}
