package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;

public class WhileStatement extends  AbstractStatement {
    public final AbstractExpression condition;
    public final AbstractStatement body;

    public WhileStatement(
        AbstractExpression condition,
        AbstractStatement body
    ) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitWhileStatement(this);
    }
}
