package mpilinski.gut.classes;

import mpilinski.gut.Gut;
import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.expressions.BinaryExpression;
import mpilinski.gut.expressions.GroupingExpression;
import mpilinski.gut.expressions.LiteralExpression;
import mpilinski.gut.expressions.UnaryExpression;
import mpilinski.gut.models.Token;
import mpilinski.gut.models.TokenType;

import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public AbstractExpression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private AbstractExpression expression() {
        return equality();
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
