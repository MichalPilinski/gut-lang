package mpilinski.gut.classes;

import mpilinski.gut.Gut;
import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.abstractions.AbstractStatement;
import mpilinski.gut.expressions.*;
import mpilinski.gut.models.Token;
import mpilinski.gut.models.TokenType;
import mpilinski.gut.statements.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<AbstractStatement> parse() {
        List<AbstractStatement> statements = new ArrayList<>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private AbstractStatement declaration() {
        try {
            if (match(TokenType.VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private AbstractStatement varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        AbstractExpression initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new VarStatement(name, initializer);
    }

    private AbstractStatement statement() {
        if(match(TokenType.IF)) return ifStatement();
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.LEFT_BRACE)) return blockStatement();

        return expressionStatement();
    }

    private AbstractStatement ifStatement() {
        consume(TokenType.LEFT_PARENTHESIS, "Expect '(' after 'if'.");
        AbstractExpression condition = expression();
        consume(TokenType.RIGHT_PARENTHESIS, "Expect ')' after if condition.");

        AbstractStatement thenBranch = statement();
        AbstractStatement elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private AbstractStatement expressionStatement() {
        AbstractExpression expr = expression();

        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new ExpressionStatement(expr);
    }

    private AbstractStatement printStatement() {
        AbstractExpression value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");

        return new PrintStatement(value);
    }

    private AbstractStatement blockStatement() {
        List<AbstractStatement> statements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return new BlockStatement(statements);
    }

    private AbstractExpression expression() {
        return assignment();
    }

    private AbstractExpression assignment() {
        AbstractExpression expr = equality();

        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            AbstractExpression value = assignment();

            if (expr instanceof VariableExpression) {
                Token name = ((VariableExpression)expr).name;
                return new AssignExpression(name, value);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private AbstractExpression equality() {
        AbstractExpression expression = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            AbstractExpression right = comparison();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private AbstractExpression comparison() {
        AbstractExpression expression = term();

        var allowedTokenTypes = new TokenType[] {
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL
        };

        while (match(allowedTokenTypes)) {
            Token operator = previous();
            AbstractExpression right = term();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private AbstractExpression term() {
        AbstractExpression expression = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            AbstractExpression right = factor();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private AbstractExpression factor() {
        AbstractExpression expression = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            AbstractExpression right = unary();

            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private AbstractExpression unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            AbstractExpression right = unary();

            return new UnaryExpression(operator, right);
        }

        return primary();
    }

    private AbstractExpression primary() {
        if (match(TokenType.FALSE)) return new LiteralExpression(false);
        if (match(TokenType.TRUE)) return new LiteralExpression(true);
        if (match(TokenType.NIL)) return new LiteralExpression(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new LiteralExpression(previous().literal);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpression(previous());
        }

        if (match(TokenType.LEFT_PARENTHESIS)) {
            AbstractExpression expr = expression();

            consume(TokenType.RIGHT_PARENTHESIS, "Expect ')' after expression.");

            return new GroupingExpression(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }

            advance();
        }
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Gut.error(token, message);
        return new ParseError();
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
