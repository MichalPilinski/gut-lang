package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class UnaryExpression extends AbstractExpression {
    public final Token operator;
    public final AbstractExpression right;

    public UnaryExpression(Token operator, AbstractExpression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnaryExpression(this);
    }
}
