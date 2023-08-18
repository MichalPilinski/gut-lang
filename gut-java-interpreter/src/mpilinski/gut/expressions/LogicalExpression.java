package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class LogicalExpression extends  AbstractExpression {
    public final AbstractExpression left;
    public final Token operator;
    public final AbstractExpression right;

    public LogicalExpression(
        AbstractExpression left,
        Token operator,
        AbstractExpression right
    ) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLogicalExpression(this);
    }
}
