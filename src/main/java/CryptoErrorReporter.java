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
import de.upb.soot.jimple.basic.PositionInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import magpiebridge.converter.PositionInfoTag;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import magpiebridge.util.SourceCodeReader;
import org.apache.commons.lang.StringUtils;
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
        List<String> repairs = brokenConstraint.getValueRange();
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
            int i = 0;
            while (i <= splitValues.length) {
              if (i < splitter.getIndex()) {
                replace.append(splitValues[i]);
                if (!replace.toString().endsWith(splitter.getSplitter()))
                  replace.append(splitter.getSplitter());
              } else if (i == splitter.getIndex()) {
                if (!repairs.isEmpty()) {
                  replace.append(repairs.get(0));
                }
              } else {
                if (i < splitValues.length) {
                  if (!replace.toString().endsWith(splitter.getSplitter()))
                    replace.append(splitter.getSplitter());
                  replace.append(splitValues[i]);
                }
              }
              i++;
            }
          }
        } else {

          if (!repairs.isEmpty()) {
            replace.append(repairs.get(0));
          }
        }
        boolean isStaticMethod = false;
        Stmt eloc = cError.getErrorLocation().getUnit().get();
        if (eloc.containsInvokeExpr()) {
          isStaticMethod = eloc.getInvokeExpr().getMethodRef().isStatic();
        }
        int paramIndex = cError.getCallSiteWithExtractedValue().getCallSite().getIndex();
        if (!isStaticMethod) {
          paramIndex++; // non static method the first operand is this-reference.
        }
        pos = positionInfo.getOperandPosition(paramIndex);
      }
    }
    if (error instanceof IncompleteOperationError) {
      IncompleteOperationError iError = (IncompleteOperationError) error;
      // TODO
    }
    if (error instanceof TypestateError) {
      TypestateError tError = (TypestateError) error;
      // TODO
    }
    if (error instanceof ForbiddenMethodError) {
      ForbiddenMethodError fError = (ForbiddenMethodError) error;
      // TODO
    }
    if (error instanceof ImpreciseValueExtractionError) {
      ImpreciseValueExtractionError ivError = (ImpreciseValueExtractionError) error;
      // TODO
    }
    if (error instanceof PredicateContradictionError) {
      PredicateContradictionError pError = (PredicateContradictionError) error;
      // TODO
    }
    if (error instanceof RequiredPredicateError) {
      RequiredPredicateError rError = (RequiredPredicateError) error;
      // TODO
    }
    String fixStr = replace.toString();
    if (!StringUtils.isNumeric(fixStr)) {
      fixStr = "\"" + fixStr + "\"";
    }
    if (pos != null) return Pair.make(pos, fixStr);
    else return null;
  }

  public List<Pair<Position, String>> getRelated(AbstractError error) throws Exception {
    List<Pair<Position, String>> related = new ArrayList<>();
    if (error instanceof ErrorWithObjectAllocation) {
      ErrorWithObjectAllocation err = (ErrorWithObjectAllocation) error;
      Unit stmt = error.getErrorLocation().getUnit().get();
      for (sync.pds.solver.nodes.Node<Statement, Val> node : err.getDataFlowPath()) {
        if (node.stmt().getUnit().isPresent()) {
          Unit n = node.stmt().getUnit().get();
          if (!n.equals(stmt)) {
            PositionInfoTag tag = (PositionInfoTag) n.getTag("PositionInfoTag");
            if (tag != null) {
              // just add stmt positions on the data flow path to related for now
              Position stmtPos = tag.getPositionInfo().getStmtPosition();
              String code = SourceCodeReader.getLinesInString(stmtPos);
              related.add(Pair.make(stmtPos, code));
            }
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
          // String msg =
          // String.format(
          // "%s violating CrySL rule for %s. %s",
          // error.getClass().getSimpleName(),
          // error.getRule().getClassName(),
          // error.toErrorMarkerString());
          String msg = error.toErrorMarkerString();
          try {
            List<Pair<Position, String>> relatedInfo = Collections.emptyList();
            Pair<Position, String> repair = getRepair(error, positionInfo);
            String code = SourceCodeReader.getLinesInString(positionInfo.getStmtPosition());
            CryptoResult res =
                new CryptoResult(
                    Kind.Diagnostic,
                    positionInfo.getStmtPosition(),
                    msg,
                    relatedInfo,
                    DiagnosticSeverity.Error,
                    repair,
                    code);
            results.add(res);
          } catch (Exception e1) {
            throw new RuntimeException(e1);
          }
        }
      }
    }
  }
}
