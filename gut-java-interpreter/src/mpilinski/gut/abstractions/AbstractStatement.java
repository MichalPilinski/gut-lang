package mpilinski.gut.abstractions;

import mpilinski.gut.statements.*;

public abstract class AbstractStatement {
  public interface Visitor<R> {
    R visitExpressionStatement(ExpressionStatement statement);
    R visitPrintStatement(PrintStatement statement);
    R visitVarStatement(VarStatement statement);
    R visitBlockStatement(BlockStatement statement);
    R visitIfStatement(IfStatement statement);
    R visitWhileStatement(WhileStatement statement);
    R visitFunctionStatement(FunctionStatement statement);
    R visitReturnStatement(ReturnStatement statement);
    R visitClassStatement(ClassStatement statement);
  }

  public abstract <R> R accept(AbstractStatement.Visitor<R> visitor);
}

