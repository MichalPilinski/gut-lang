package mpilinski.gut.abstractions;

import mpilinski.gut.expressions.BinaryExpression;
import mpilinski.gut.expressions.GroupingExpression;
import mpilinski.gut.expressions.LiteralExpression;
import mpilinski.gut.expressions.UnaryExpression;

public abstract class AbstractExpression {
    public interface Visitor<R> {
        R visitBinaryExpression(BinaryExpression expr);
        R visitGroupingExpression(GroupingExpression expr);
        R visitLiteralExpression(LiteralExpression expr);
        R visitUnaryExpression(UnaryExpression expr);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
