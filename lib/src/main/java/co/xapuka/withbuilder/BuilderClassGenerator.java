package co.xapuka.withbuilder;

import java.io.PrintWriter;
import java.util.Map;

public final class BuilderClassGenerator {

private final ClassAndPackageName classAndPackageName;

private final  Map<String, String> fieldsAndTheirTypes;

private final PrintWriter pw;

    public BuilderClassGenerator(ClassAndPackageName classAndPackageName,
                                 Map<String, String> fieldsAndTheirTypes,
                                 PrintWriter pw) {
        this.classAndPackageName = classAndPackageName;
        this.fieldsAndTheirTypes = fieldsAndTheirTypes;
        this.pw = pw;
    }

    public void writePrivateDefaultConstructor() {
        pw.printf("private %s() {};\n", classAndPackageName.getBuilderClassName());
    }

    public void writeInit() {
        String builderName = classAndPackageName.getBuilderClassName();
        pw.printf("package %s;\n", classAndPackageName.getPackageName())
                .printf("public final class %s {\n", classAndPackageName.getBuilderClassName())
                .printf("public static %s newInstance() {\n", builderName)
                .printf(returnAndClose(String.format("new %s()", builderName)));


    }

    public void withBuildMethod() {
        final String targetClassName = classAndPackageName.getClassName();

        pw.printf("public %s build() %s", targetClassName, openingBracketLineBreak())
                .printf("%s instance = new %s();\n", targetClassName, targetClassName);
        for (String field : fieldsAndTheirTypes.keySet()) {
            pw.printf("instance.set%s(this.%s);\n", capitalize(field), field);
        }
        pw.printf(returnAndClose("instance"));
    }

    public void writeFields() {
        for (Map.Entry<String, String> entry : fieldsAndTheirTypes.entrySet()) {
            pw.printf("private %s %s;\n", entry.getValue(), entry.getKey());
        }
    }

    public void writeMutators() {
        for (Map.Entry<String, String> entry : fieldsAndTheirTypes.entrySet()) {
            final String parameterType = entry.getValue();
            final String parameterName = entry.getKey();
            pw.printf("public %s with%s(%s %s) %s", classAndPackageName.getBuilderClassName(),
                            capitalize(parameterName), parameterType, parameterName, openingBracketLineBreak())
                    .printf("this.%s = %s;\n", parameterName ,parameterName)
                    .printf(returnAndClose("this"));
        }
    }

    public void writeEnd() {
        pw.printf(closingBracketLineBreak());
    }

    String closingBracketLineBreak() {
        return "}\n";
    }

    String openingBracketLineBreak() {
        return "{\n";
    }

    String returnAndClose(String value) {
        return String.format("return %s;\n%s", value, closingBracketLineBreak());
    }

    public static String capitalize(String value) {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException(BuilderClassGenerator.class.getSimpleName()+"#capitalize does not accept null or blank strings");
        }
        final String s = value.substring(0, 1).toUpperCase() +
                value.substring(1).toLowerCase();
        return s;
    }
}
