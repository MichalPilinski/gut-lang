package mpilinski.gut.statements;

import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.models.Token;

import java.util.List;

public class ClassStatement extends  AbstractStatement {
    public final Token name;
    public final List<FunctionStatement> methods;

    public ClassStatement(
        Token name,
        List<FunctionStatement> methods
    ) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitClassStatement(this);
    }
}
