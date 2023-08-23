package mpilinski.gut.classes;

import mpilinski.gut.Gut;
import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.abstractions.GutCallable;
import mpilinski.gut.models.GutFunction;
import mpilinski.gut.errors.ReturnException;
import mpilinski.gut.errors.RuntimeError;
import mpilinski.gut.expressions.*;
import mpilinski.gut.foreign.ClockForeign;
import mpilinski.gut.models.Token;
import mpilinski.gut.models.TokenType;
import mpilinski.gut.statements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements AbstractExpression.Visitor<Object>, AbstractStatement.Visitor<Void> {
    public final Environment globals = new Environment();
    private Environment environment = globals;

    private final Map<AbstractExpression, Integer> locals = new HashMap<>();

    public Interpreter() {
        registerForeigns();
    }

    private void registerForeigns() {
        globals.define("clock", new ClockForeign());
    }

    public void interpret(List<AbstractStatement> statements) {
        try {
            for (AbstractStatement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Gut.runtimeError(error);
        }
    }

    private void execute(AbstractStatement statement) {
        statement.accept(this);
    }

    void resolve(AbstractExpression expression, int depth) {
        locals.put(expression, depth);
    }

    public void executeBlock(List<AbstractStatement> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (AbstractStatement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();

            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));

        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        while (isTruthy(evaluate(statement.condition))) {
            execute(statement.body);
        }

        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {
        GutFunction function = new GutFunction(statement, environment);
        environment.define(statement.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        Object value = null;
        if(statement.value != null) value = evaluate(statement.value);

        throw new ReturnException(value);
    }

    @Override
    public  Void visitBlockStatement(BlockStatement statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitVarStatement(VarStatement statement) {
        Object value = null;
        if (statement.initializer != null) {
            value = evaluate(statement.initializer);
        }

        environment.define(statement.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement statement) {
        if (isTruthy(evaluate(statement.condition))) {
            execute(statement.thenBranch);
            return null;
        }

        if (statement.elseBranch != null) {
            execute(statement.elseBranch);
        }

        return null;
    }

    @Override
    public Object visitAssignExpression(AssignExpression expression) {
        Object value = evaluate(expression.value);

        Integer distance = locals.get(expression);
        if (distance != null) {
            environment.assignAt(distance, expression.name, value);
        } else {
            globals.assign(expression.name, value);
        }

        return value;
    }

    @Override
    public Object visitVariableExpression(VariableExpression expression) {
        return lookUpVariable(expression.name, expression);
    }

    @Override
    public Object visitLiteralExpression(LiteralExpression expression) {
        return expression.value;
    }

    @Override
    public Object visitLogicalExpression(LogicalExpression expression) {
        Object left = evaluate(expression.left);

        if (expression.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expression.right);
    }

    @Override
    public Object visitGroupingExpression(GroupingExpression expression) {
        return evaluate(expression.expression);
    }
    
    @Override
    public Object visitUnaryExpression(UnaryExpression expression) {
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return -(double) right;
            case BANG:
                return !isTruthy(right);
        };

        return null;
    }

    // TODO: Split into two functions, one with all "checkNumberOperands" cases
    @Override
    public Object visitBinaryExpression(BinaryExpression expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case GREATER:
                checkNumberOperands(expression.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expression.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expression.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expression.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                checkNumberOperands(expression.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expression.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expression.operator, left, right);
                return (double) left * (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expression.operator,
                    "Operands must be two numbers or two strings.");
        }

        return null;
    }

    @Override
    public Object visitCallExpression(CallExpression expression) {
        Object callee = evaluate(expression.callee);

        List<Object> arguments = new ArrayList<>();
        for (AbstractExpression argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }

        if(!(callee instanceof GutCallable function)) {
            throw new RuntimeError(expression.paren, "Can only call functions and classes.");
        }

        if(arguments.size() != function.arity()) {
            throw new RuntimeError(expression.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    private Object lookUpVariable(Token name, AbstractExpression expression) {
        Integer distance = locals.get(expression);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;

        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private Object evaluate(AbstractExpression expression) {
        return expression.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;

        return true;
    }
}
