package mpilinski.gut;

import java.util.*;

public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return",TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int startIndex = 0;
    private int currentIndex = 0;
    private int lineCount = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while(!isAtEnd()) {
            startIndex = currentIndex;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, lineCount));
        return tokens;
    }

    private void scanToken() {
        char lastChar = advance();

        if(tryScanSingleToken(lastChar)) return;
        if(tryScanOperator(lastChar)) return;
        if(tryScanComment(lastChar)) return;

        if(Arrays.asList(' ', '\r', '\t').contains(lastChar)) return;
        if(lastChar == '\n') {
            lineCount++;
            return;
        }

        if(tryScanString(lastChar)) return;
        if(tryScanNumber(lastChar)) return;

        if(tryScanIdentifier(lastChar)) return;

        Gut.error(lineCount, "Unexpected character.");
    }

    private boolean tryScanSingleToken(char lastChar) {
        var scannedToken = switch (lastChar) {
            case '(' -> TokenType.LEFT_PARENTHESIS;
            case ')' -> TokenType.RIGHT_PARENTHESIS;
            case '{' -> TokenType.LEFT_BRACE;
            case '}' -> TokenType.RIGHT_BRACE;
            case ',' -> TokenType.COMMA;
            case '.' -> TokenType.DOT;
            case '-' -> TokenType.MINUS;
            case '+' -> TokenType.PLUS;
            case ';' -> TokenType.SEMICOLON;
            case '*' -> TokenType.STAR;
            default -> null;
        };

        if(scannedToken == null) return false;

        addToken(scannedToken);
        return true;
    }

    private boolean tryScanOperator(char lastChar) {
        var scannedToken = switch (lastChar) {
            case '!' -> match('=') ? TokenType.BANG_EQUAL : TokenType.BANG;
            case '=' -> match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL;
            case '<' -> match('=') ? TokenType.LESS_EQUAL : TokenType.LESS;
            case '>' -> match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER;
            default -> null;
        };

        if(scannedToken == null) return false;

        addToken(scannedToken);
        return true;
    }

    private boolean tryScanComment(char lastChar) {
        if(lastChar != '/') return false;

        if(!match('/')) {
            addToken(TokenType.SLASH);
            return true;
        }

        while(peek() != '\n' && !isAtEnd()) {
            advance();
        }

        return true;
    }

    private boolean tryScanString(char lastChar) {
        if(lastChar != '"') return false;

        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') lineCount++;
            advance();
        }

        if(isAtEnd()) {
            Gut.error(lineCount, "Unterminated string.");
            return true;
        }

        advance();

        String value = source.substring(startIndex + 1, currentIndex - 1);
        addToken(TokenType.STRING, value);

        return true;
    }

    private boolean tryScanNumber(char lastChar) {
        if(!isDigit(lastChar)) return false;

        while (isDigit(peek())) advance();

        if(peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        var parsedNumber = Double.parseDouble(source.substring(startIndex, currentIndex));
        addToken(TokenType.NUMBER, parsedNumber);

        return true;
    }

    private boolean tryScanIdentifier(char lastChar) {
        if(!isAlpha(lastChar)) return false;

        while(isAlphaNumeric(peek())) advance();

        String text = source.substring(startIndex, currentIndex);

        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;

        addToken(type);
        return true;
    }


    private boolean isAtEnd() {
        return currentIndex >= source.length();
    }

    private char advance() {
        return source.charAt(currentIndex++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(startIndex, currentIndex);
        tokens.add(new Token(type, text, literal, lineCount));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(currentIndex) != expected) return false;

        currentIndex++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';

        return source.charAt(currentIndex);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char peekNext() {
        if (currentIndex + 1 >= source.length()) return '\0';
        return source.charAt(currentIndex + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
