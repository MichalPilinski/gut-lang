package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;

public class IfStatement extends  AbstractStatement {
    public final AbstractExpression condition;
    public final AbstractStatement thenBranch;
    public final AbstractStatement elseBranch;

    public IfStatement(
        AbstractExpression condition,
        AbstractStatement thenBranch,
        AbstractStatement elseBranch
    ) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitIfStatement(this);
    }
}
