package mpilinski.gut.models;

import mpilinski.gut.abstractions.GutCallable;
import mpilinski.gut.classes.Interpreter;

import java.util.List;
import java.util.Map;

public class GutClass implements GutCallable {
    final String name;
    private final Map<String, GutFunction> methods;

    public GutClass(String name, Map<String, GutFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        GutFunction initializer = findMethod("init");
        if (initializer == null) return 0;

        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        GutInstance instance = new GutInstance(this);

        GutFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    GutFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }
}
