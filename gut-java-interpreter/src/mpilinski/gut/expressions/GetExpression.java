package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class GetExpression extends  AbstractExpression {
    public final AbstractExpression object;
    public final Token name;

    public GetExpression(
        AbstractExpression object,
        Token name
    ) {
        this.object = object;
        this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitGetExpression(this);
    }
}
