package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.models.Token;

public class VarStatement extends  AbstractStatement {
    public final Token name;
    public final AbstractExpression initializer;

    public VarStatement(
        Token name,
        AbstractExpression initializer
    ) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitVarStatement(this);
    }
}
