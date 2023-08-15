package mpilinski.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class AstGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        generateAst(outputDir, Arrays.asList(
                "Binary   : AbstractExpression left, Token operator, AbstractExpression right",
                "Grouping : AbstractExpression expression",
                "Literal  : Object value",
                "Unary    : Token operator, AbstractExpression right"
        ));
    }

    private static void generateAst(String outputDir, List<String> types) throws IOException {
        generateBaseClass(outputDir, types);

        for(String type: types) {
            generateExpressionType(outputDir, type);
        }
    }

    private static void generateExpressionType(String basePath, String type) throws IOException {
        String typeName = type.split(":")[0].trim();
        String className = typeName + "Expression";

        String path = basePath + "/" + className + ".autogen.java";
        try(PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package mpilinski.gut.expressions;");
            writer.println();

            writer.println("import mpilinski.gut.abstractions.AbstractExpression;");
            writer.println("import mpilinski.gut.models.Token;");
            writer.println();

            writer.println("public class " + className + " extends  AbstractExpression {");

            // Fields
            String[] fields = type.split(":")[1].trim().split(", ");
            for (String field : fields) {
                String fieldType = field.split(" ")[0];
                String fieldName = field.split(" ")[1];
                writer.println("    public final " + fieldType + " " + fieldName + ";");
            }
            writer.println();

            // Constructor
            writer.println("    public " + className + "(");
            for (int i = 0; i < fields.length; i++) {
                String fieldType = fields[i].split(" ")[0];
                String fieldName = fields[i].split(" ")[1];

                var lineEnding = i != fields.length - 1 ? "," : "";
                writer.println("        " + fieldType + " " + fieldName + lineEnding);
            }
            writer.println("    ) {");
            for (String field : fields) {
                String fieldName = field.split(" ")[1];
                writer.println("        this." + fieldName + " = " + fieldName + ";");
            }
            writer.println("    }");
            writer.println();

            // Visitor
            writer.println("    @Override");
            writer.println("    public <R> R accept(Visitor<R> visitor) {");
            writer.println("        return visitor.visit" + className + "(this);");
            writer.println("    }");

            writer.println("}");
        }
    }

    private static void generateBaseClass(String basePath, List<String> types) throws IOException {
        String path = basePath + "/AbstractExpression.autogen.java";

        try(PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package mpilinski.gut.abstractions;");
            writer.println();

            writer.println("import mpilinski.gut.expressions.BinaryExpression;");
            writer.println("import mpilinski.gut.expressions.GroupingExpression;");
            writer.println("import mpilinski.gut.expressions.LiteralExpression;");
            writer.println("import mpilinski.gut.expressions.UnaryExpression;");
            writer.println();

            writer.println("public abstract class AbstractExpression {");

            defineVisitor(writer, types);
        }

    }

    private static void defineVisitor(PrintWriter writer, List<String> types) {
        writer.println("  public interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + "Expression(" + typeName + "Expression expr);");
        }

        writer.println("  }");
        writer.println();
    }
}