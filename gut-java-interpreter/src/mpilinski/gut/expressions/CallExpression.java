package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

import java.util.List;

public class CallExpression extends  AbstractExpression {
    public final AbstractExpression callee;
    public final Token paren;
    public final List<AbstractExpression> arguments;

    public CallExpression(
        AbstractExpression callee,
        Token paren,
        List<AbstractExpression> arguments
    ) {
        this.callee = callee;
        this.paren = paren;
        this.arguments = arguments;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCallExpression(this);
    }
}
