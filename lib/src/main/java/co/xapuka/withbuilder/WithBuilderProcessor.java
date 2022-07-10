/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package co.xapuka.withbuilder;

import co.xapuka.withbuilder.exception.NoConstructorFound;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.NONE;

public class WithBuilderProcessor extends AbstractProcessor {
    public boolean someLibraryMethod() {
        return true;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(WithBuilder.class);
        if(annotatedElements != null && !annotatedElements.isEmpty()) {
            pm("Processing WithBuilder...");
        }
        for(Element annotatedElement: annotatedElements) {
            final WithBuilder annotation = annotatedElement.getAnnotation(WithBuilder.class);
            final List<? extends Element> enclosedElements = annotatedElement.getEnclosedElements();
            final boolean isDebugActive = annotation.debug();
            if(isDebugActive) {
                pm("In Debugging");
                enclosedElements.forEach(el -> {
                    pm(el.getSimpleName() + ":" + el.asType() + ":" +el.asType().getKind());
                    if(el instanceof ExecutableElement) {
                        pm("ExecutableElement: " + ((ExecutableElement)el).getSimpleName().toString());
                    }
                });
                return false;
            }


            Map<Boolean, List<Element>> partion = enclosedElements.stream().collect(Collectors.partitioningBy(this::onlyInstanceFields));
            Map<String, String> fieldsAndTheirTypes =
                    partion.get(true).stream().collect(Collectors.toMap(e -> e.getSimpleName().toString(), e -> e.asType().toString()));

            boolean didValidate = validateSetters(partion.get(false).stream(), fieldsAndTheirTypes, annotatedElement);

            if(!didValidate) {
                return true;
            }

            TypeElement typeElement;
            try {
                typeElement = getTypeElement(annotatedElement);
            } catch(NoConstructorFound e) {
                return reportError("No constructor found!", annotatedElement);
            }
            ClassAndPackageName classAndPackageName = ClassAndPackageName.from(typeElement, annotation.suffix());

            try {
                writeFile(classAndPackageName, fieldsAndTheirTypes);
            } catch (IOException e) {
                return reportError(e.getMessage(), annotatedElement);
            }
        };

        return false;
    }

    private boolean validateSetters(Stream<Element> noFields, Map<String, String> fieldsAndTheirTypes, Element annotatedElement) {
        List<Element> setters = noFields.collect(Collectors.partitioningBy(this::onlySetter)).get(true);

        Collection<String> onlyFieldNames = new ArrayList<>(fieldsAndTheirTypes.keySet());

        List<String> settersNames = setters.stream().map(Element::getSimpleName).map(Name::toString).collect(Collectors.toList());
        for (String fieldName: fieldsAndTheirTypes.keySet()) {
            String setterName = "set"+capitalize(fieldName);

            if(settersNames.stream().filter(n -> n.equals(setterName)).count() != 0) {
                onlyFieldNames.remove(fieldName);
            }
        }
        onlyFieldNames.stream().forEach( n -> reportError("No setter found for field " + n , annotatedElement));

        if(onlyFieldNames.size() != 0) {
            return false;
        }

        return true;
    }

    private  boolean onlySetter(Element element) {
        if(!(element instanceof ExecutableElement)) {
            return false;
        }
        ExecutableElement executableElement = (ExecutableElement) element;
        if(executableElement.getParameters().size() != 1) {
            return false;
        }
        if(executableElement.getReturnType().getKind() != TypeKind.VOID) {
            return false;
        }
        if(!executableElement.getSimpleName().toString().matches("^set.*")){
            return false;
        }
        return true;
    }

    private boolean onlyInstanceFields(Element e) {
        return (FIELD == e.getKind() ) &&
                e.getModifiers().stream().filter(m -> (FINAL == m || STATIC == m)).count() == 0;
    }

    private void writeFile(ClassAndPackageName classAndPackageName, Map<String, String> fieldsAndTheirTypes) throws IOException {
        final String builderName = classAndPackageName.getBuilderClassName();
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile( classAndPackageName.getPackageName()+"."+ classAndPackageName.getBuilderClassName());
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.println("package " + classAndPackageName.getPackageName() + ";");
            out.println("public final class " + builderName + "{");

            writeInit(builderName, out);

            writeMutators(fieldsAndTheirTypes, builderName, out);

            withBuildMethod(classAndPackageName, fieldsAndTheirTypes.keySet(), out);

            writeConstructor(builderName, out);

            writeFields(fieldsAndTheirTypes, builderName, out);

            out.println("}");
        }
    }

    private void writeConstructor(String builderName, PrintWriter out) {
        out.println("private " + builderName + "(){};");
    }

    private void writeInit(String builderName, PrintWriter out) {
        out.println("public static "+ builderName + " newInstance() {");
        out.println("return new "+ builderName + "();");
        out.println("}");
    }

    private void withBuildMethod(ClassAndPackageName classAndPackageName, Collection<String> fields, PrintWriter out) {
        final String targetClassName = classAndPackageName.getClassName();
        out.println("public "+ targetClassName +" build() {");
        final String instance = "instance";
        out.println(targetClassName + " " + instance + " = new " + targetClassName +"();");
        for(String field: fields) {
            out.println(instance + ".set" + capitalize(field) + "(this."+field+");");
        }
        out.println("return " + instance + ";");
        out.println("}");
    }

    private void writeFields(Map<String, String> fieldsAndTheirTypes, String builderName, PrintWriter out) {
        for (Map.Entry<String, String> entry : fieldsAndTheirTypes.entrySet()) {
            out.println("private " + entry.getValue() + " " + entry.getKey() + ";");
        }
    }

    private void writeMutators(Map<String, String> fieldsAndTheirTypes, String builderName, PrintWriter out) {
        for (Map.Entry<String, String> entry : fieldsAndTheirTypes.entrySet()) {
            final String parameterType = entry.getValue();
            final String parameterName = entry.getKey();
            out.println("public " + builderName + " with" +
                    capitalize(parameterName) +
                    "(" + parameterType + " " + parameterName + "){");
            out.println("this." + parameterName + " = " + parameterName + ";");
            out.println("return this;");
            out.println("}");
        }
    }

    private String capitalize(String parameterName) {
        final String s = parameterName.substring(0, 1).toUpperCase() +
                parameterName.substring(1).toLowerCase();
        return s;
    }

    private TypeElement getTypeElement(Element element) throws NoConstructorFound {
        TypeElement typeElement = element.getEnclosedElements()
                .stream()
                .filter(e -> CONSTRUCTOR == e.getKind())
                .map(e -> e.getEnclosingElement())
                .map(e -> (TypeElement) e)
                .reduce((x, y) -> x)
                .orElseThrow(NoConstructorFound::new);
        return typeElement;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(WithBuilder.class.getName());
    }

    private void pm(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    private boolean reportError (String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
        return true;
    }
}
