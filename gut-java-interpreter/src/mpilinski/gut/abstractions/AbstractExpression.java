package mpilinski.gut.abstractions;

import mpilinski.gut.expressions.*;

public abstract class AbstractExpression {
    public interface Visitor<R> {
        R visitBinaryExpression(BinaryExpression expr);
        R visitGroupingExpression(GroupingExpression expr);
        R visitLiteralExpression(LiteralExpression expr);
        R visitUnaryExpression(UnaryExpression expr);
        R visitVariableExpression(VariableExpression expression);
        R visitAssignExpression(AssignExpression expression);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
