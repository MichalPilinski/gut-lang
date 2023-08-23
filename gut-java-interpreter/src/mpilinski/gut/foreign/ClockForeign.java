package mpilinski.gut.foreign;

import mpilinski.gut.abstractions.GutCallable;
import mpilinski.gut.classes.Interpreter;

import java.util.List;

public class ClockForeign implements GutCallable {
    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double)System.currentTimeMillis() / 1000.0;
    }

    @Override
    public String toString() {
        return "<native clock fn>";
    }
}
