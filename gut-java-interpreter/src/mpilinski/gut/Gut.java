package mpilinski.gut;

import mpilinski.gut.abstractions.AbstractExpression;
import mpilinski.gut.classes.Interpreter;
import mpilinski.gut.classes.Parser;
import mpilinski.gut.classes.Scanner;
import mpilinski.gut.errors.RuntimeError;
import mpilinski.gut.models.Token;
import mpilinski.gut.models.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Gut {
    private static final Interpreter interpreter = new Interpreter();

    private static boolean hadError;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {

        System.out.print("kanapka");

        if(args.length > 1) {
            System.out.println("Too much arguments provided!");
            System.out.println("Usage: jgut [script ]");

            System.exit(64);
        } else if (args.length == 1) {
            runScript(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runScript(String path) throws IOException {
        var filePath = Paths.get(path);
        byte[] fileBytes = Files.readAllBytes(filePath);

        run(new String(fileBytes, Charset.defaultCharset()));

        if(hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        var input = new InputStreamReader(System.in);
        var reader = new BufferedReader(input);

        for(;;) {
            System.out.print("> ");
            var line = reader.readLine();

            if(line == null || line.equals("exit")) break;
            run(line);

            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        AbstractExpression expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        interpreter.interpret(expression);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.lineNumber + "]");
        hadRuntimeError = true;
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.lineNumber, " at end", message);
        } else {
            report(token.lineNumber, " at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int line, String location, String message) {
        var output = "[line" + line + "] Error" + location + ": " + message;
        System.err.println(output);
        
        hadError = true;
    }
}

