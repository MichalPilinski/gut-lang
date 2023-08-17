package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;


public class ExpressionStatement extends  AbstractStatement {
    public final AbstractExpression expression;

    public ExpressionStatement(
        AbstractExpression expression
    ) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}
