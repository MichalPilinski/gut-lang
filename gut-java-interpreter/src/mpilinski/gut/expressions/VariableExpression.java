package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class VariableExpression extends  AbstractExpression {
    public final Token name;

    public VariableExpression(
        Token name
    ) {
        this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitVariableExpression(this);
    }
}
