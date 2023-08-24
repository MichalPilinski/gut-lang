package mpilinski.gut.classes;

import mpilinski.gut.Gut;
import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.expressions.*;
import mpilinski.gut.models.ClassType;
import mpilinski.gut.models.FunctionType;
import mpilinski.gut.models.Token;
import mpilinski.gut.statements.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class Resolver implements AbstractExpression.Visitor<Void>, AbstractStatement.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
//> kanapka
//<
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        beginScope();
        resolve(statement.statements);
        endScope();

        return null;
    }

    @Override
    public Void visitVarStatement(VarStatement statement) {
        declare(statement.name);

        if(statement.initializer != null) {
            resolve(statement.initializer);
        }

        define(statement.name);

        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {
        declare(statement.name);
        define(statement.name);

        resolveFunction(statement, FunctionType.FUNCTION);

        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        resolve(statement.expression);

        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement statement) {
        resolve(statement.condition);
        resolve(statement.thenBranch);
        if (statement.elseBranch != null) resolve(statement.elseBranch);

        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        resolve(statement.expression);

        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        if (currentFunction == FunctionType.NONE) {
            Gut.error(statement.keyword, "Can't return from top-level code.");
        }

        if (statement.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Gut.error(statement.keyword, "Can't return a value from an initializer.");
            }

            resolve(statement.value);
        }

        return null;
    }

    @Override
    public Void visitClassStatement(ClassStatement statement) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(statement.name);
        define(statement.name);

        beginScope();
        scopes.peek().put("this", true);

        for (FunctionStatement method : statement.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }

            resolveFunction(method, declaration);
        }

        endScope();

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        resolve(statement.condition);
        resolve(statement.body);

        return null;
    }

    @Override
    public Void visitVariableExpression(VariableExpression expression) {
        if(!scopes.isEmpty() && scopes.peek().get(expression.name.lexeme) == Boolean.FALSE) {
            Gut.error(expression.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expression, expression.name);

        return null;
    }

    @Override
    public Void visitAssignExpression(AssignExpression expression) {
        resolve(expression.value);
        resolveLocal(expression, expression.name);

        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expression) {
        resolve(expression.left);
        resolve(expression.right);

        return null;
    }

    @Override
    public Void visitCallExpression(CallExpression expression) {
        resolve(expression.callee);

        for (AbstractExpression argument : expression.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGetExpression(GetExpression expression) {
        resolve(expression.object);
        return null;
    }

    @Override
    public Void visitSetExpression(SetExpression expression) {
        resolve(expression.value);
        resolve(expression.object);
        return null;
    }

    @Override
    public Void visitThisExpression(ThisExpression expression) {
        if (currentClass == ClassType.NONE) {
            Gut.error(expression.keyword, "Can't use 'this' outside of a class.");

            return null;
        }

        resolveLocal(expression, expression.keyword);
        return null;
    }

    @Override
    public Void visitGroupingExpression(GroupingExpression expression) {
        resolve(expression.expression);

        return null;
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visitLogicalExpression(LogicalExpression expression) {
        resolve(expression.left);
        resolve(expression.right);

        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression expression) {
        resolve(expression.right);

        return null;
    }

    private void resolveFunction(FunctionStatement function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();

        currentFunction = enclosingFunction;
    }

    private void resolveLocal(AbstractExpression expression, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void declare(Token name) {
        if(scopes.isEmpty()) return;

        Map<String, Boolean> currentScope = scopes.peek();
        if (currentScope.containsKey(name.lexeme)) {
            Gut.error(name, "Already a variable with this name in this scope.");
        }

        currentScope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if(scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    public void resolve(List<AbstractStatement> statements) {
        for (AbstractStatement statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(AbstractStatement statement) {
        statement.accept(this);
    }

    private void resolve(AbstractExpression expression) {
        expression.accept(this);
    }
}
