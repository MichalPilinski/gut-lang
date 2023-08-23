package mpilinski.gut.abstractions;

import mpilinski.gut.classes.Interpreter;

import java.util.List;

public interface GutCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
