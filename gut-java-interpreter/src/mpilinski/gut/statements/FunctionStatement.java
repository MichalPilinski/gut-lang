package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.models.Token;

import java.util.List;

public class FunctionStatement extends  AbstractStatement {
    public final Token name;
    public final List<Token> params;
    public final List<AbstractStatement> body;

    public FunctionStatement(
        Token name,
        List<Token> params,
        List<AbstractStatement> body
    ) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFunctionStatement(this);
    }
}
