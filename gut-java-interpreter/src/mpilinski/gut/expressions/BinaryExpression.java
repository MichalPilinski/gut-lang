package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class BinaryExpression extends AbstractExpression {
    public final AbstractExpression left;
    public final Token operator;
    public final AbstractExpression right;

    public BinaryExpression(
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
        return visitor.visitBinaryExpression(this);
    }
}