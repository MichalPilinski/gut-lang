package mpilinski.gut.expressions;

import mpilinski.gut.abstractions.AbstractExpression;

public class GroupingExpression extends AbstractExpression {
    public final AbstractExpression expression;

    public GroupingExpression(AbstractExpression expression) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitGroupingExpression(this);
    }
}
