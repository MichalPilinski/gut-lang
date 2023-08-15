package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;

public class LiteralExpression extends AbstractExpression {
    public final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }
}
