package mpilinski.gut.models;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int lineNumber;

    public Token(TokenType type, String lexeme, Object literal, int lineNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.lineNumber = lineNumber;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
