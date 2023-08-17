package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class AssignExpression extends  AbstractExpression {
    public final Token name;
    public final AbstractExpression value;

    public AssignExpression(
        Token name,
        AbstractExpression value
    ) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignExpression(this);
    }
}
