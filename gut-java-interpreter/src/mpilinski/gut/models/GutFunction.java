package mpilinski.gut.models;

import mpilinski.gut.abstractions.GutCallable;
import mpilinski.gut.classes.Environment;
import mpilinski.gut.classes.Interpreter;
import mpilinski.gut.errors.ReturnException;
import mpilinski.gut.statements.FunctionStatement;
import java.util.List;

public class GutFunction implements GutCallable {
    private final FunctionStatement declaration;
    private final Environment closure;

    public GutFunction(FunctionStatement declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try{
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnException returnValue) {
            return returnValue.value;
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
