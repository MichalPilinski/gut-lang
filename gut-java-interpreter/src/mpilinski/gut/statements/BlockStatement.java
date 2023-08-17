package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractStatement;

import java.util.List;

public class BlockStatement extends  AbstractStatement {
    public final List<AbstractStatement> statements;

    public BlockStatement(
        List<AbstractStatement> statements
    ) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitBlockStatement(this);
    }
}
