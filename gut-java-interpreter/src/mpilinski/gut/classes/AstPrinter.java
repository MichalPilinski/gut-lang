package mpilinski.gut.classes;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.expressions.*;

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

    @Override
    public String visitVariableExpression(VariableExpression expression) {
        return null;
    }

    @Override
    public String visitAssignExpression(AssignExpression expression) {
        return null;
    }

    @Override
    public String visitLogicalExpression(LogicalExpression expression) {
        return null;
    }

    @Override
    public String visitCallExpression(CallExpression expression) {
        return null;
    }

    @Override
    public String visitGetExpression(GetExpression expression) {
        return null;
    }

    @Override
    public String visitSetExpression(SetExpression expression) {
        return null;
    }

    @Override
    public String visitThisExpression(ThisExpression expression) {
        return null;
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
