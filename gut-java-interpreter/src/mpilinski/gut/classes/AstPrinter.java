package mpilinski.gut.classes;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.expressions.BinaryExpression;
import mpilinski.gut.expressions.GroupingExpression;
import mpilinski.gut.expressions.LiteralExpression;
import mpilinski.gut.expressions.UnaryExpression;

public class AstPrinter implements AbstractExpression.Visitor<String> {
    String print(AbstractExpression expression) {
        return expression.accept(this);
    }

    @Override
    public String visitBinaryExpression(BinaryExpression expression) {
        return parenthesize(
                expression.operator.lexeme,
                expression.left,
                expression.right
        );
    }

    @Override
    public String visitGroupingExpression(GroupingExpression expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitLiteralExpression(LiteralExpression expression) {
        if (expression.value == null) return "nil";

        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(UnaryExpression expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    private String parenthesize(String name, AbstractExpression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (AbstractExpression expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
