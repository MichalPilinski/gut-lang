package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;

public class PrintStatement extends AbstractStatement {
    public final AbstractExpression expression;

    public PrintStatement(
        AbstractExpression expression
    ) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPrintStatement(this);
    }
}
