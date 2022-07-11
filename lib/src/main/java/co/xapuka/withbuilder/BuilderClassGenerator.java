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
        pw.printf("private %s(){};\n", classAndPackageName.getBuilderClassName());
    }

    public void writeInit() {
        String builderName = classAndPackageName.getBuilderClassName();
        pw.printf("package %s;\n", classAndPackageName.getPackageName())
                .printf("public final class %s {\n", classAndPackageName.getBuilderClassName())
                .printf("public static %s newInstance() {\n", builderName)
                .printf("return new %s();\n", builderName)
                .printf(closingBracketLineBreak());


    }

    public void withBuildMethod() {
        final String targetClassName = classAndPackageName.getClassName();

        pw.printf("public %s build() {\n", targetClassName)
                .printf("%s instance = new %s();\n", targetClassName, targetClassName);
        for (String field : fieldsAndTheirTypes.keySet()) {
            pw.printf("instance.set%s(this.%s);\n", capitalize(field), field);
        }
        pw.printf("return instance;\n")
                .printf(closingBracketLineBreak());
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
            pw.printf("public %s with%s(%s %s){\n", classAndPackageName.getBuilderClassName(),
                            capitalize(parameterName), parameterType, parameterName )
                    .printf("this.%s = %s;\n", parameterName ,parameterName)
                    .printf("return this;\n")
                    .printf(closingBracketLineBreak());
        }
    }

    public void writeEnd() {
        pw.printf(closingBracketLineBreak());
    }

    private String closingBracketLineBreak() {
        return "}\n";
    }

    public static String capitalize(String value) {
        final String s = value.substring(0, 1).toUpperCase() +
                value.substring(1).toLowerCase();
        return s;
    }
}
