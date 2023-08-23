package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.models.Token;

public class ReturnStatement extends  AbstractStatement {
    public final Token keyword;
    public final AbstractExpression value;

    public ReturnStatement(
        Token keyword,
        AbstractExpression value
    ) {
        this.keyword = keyword;
        this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitReturnStatement(this);
    }
}
