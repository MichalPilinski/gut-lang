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
        R visitLogicalExpression(LogicalExpression expression);
        R visitCallExpression(CallExpression expression);
        R visitGetExpression(GetExpression expression);
        R visitSetExpression(SetExpression expression);
        R visitThisExpression(ThisExpression expression);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
