package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class SetExpression extends  AbstractExpression {
    public final AbstractExpression object;
    public final Token name;
    public final AbstractExpression value;

    public SetExpression(
        AbstractExpression object,
        Token name,
        AbstractExpression value
    ) {
        this.object = object;
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitSetExpression(this);
    }
}
