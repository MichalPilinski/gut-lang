package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.models.Token;

public class ThisExpression extends  AbstractExpression {
    public final Token keyword;

    public ThisExpression(
        Token keyword
    ) {
        this.keyword = keyword;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitThisExpression(this);
    }
}
