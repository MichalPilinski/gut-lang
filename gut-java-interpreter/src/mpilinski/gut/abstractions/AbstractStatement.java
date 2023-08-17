package mpilinski.gut.abstractions;

import mpilinski.gut.statements.BlockStatement;
import mpilinski.gut.statements.ExpressionStatement;
import mpilinski.gut.statements.PrintStatement;
import mpilinski.gut.statements.VarStatement;

public abstract class AbstractStatement {
  public interface Visitor<R> {
    R visitExpressionStatement(ExpressionStatement statement);
    R visitPrintStatement(PrintStatement statement);
    R visitVarStatement(VarStatement statement);
    R visitBlockStatement(BlockStatement statement);
  }

  public abstract <R> R accept(AbstractStatement.Visitor<R> visitor);
}

