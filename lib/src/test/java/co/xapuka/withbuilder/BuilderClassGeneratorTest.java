package co.xapuka.withbuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BuilderClassGeneratorTest {

    private StringWriter sw;

    private ClassAndPackageName classAndPackageName;
    private Map<String, String> fieldsAndTheirTypes;

    @BeforeEach
    void init() {
        sw = new StringWriter();
        classAndPackageName = new ClassAndPackageName() {
            @Override
            public String getClassName() {
                return "Foo";
            }

            @Override
            public String getBuilderClassName() {
                return "FooBuilder";
            }

            @Override
            public String getPackageName() {
                return "co.xapuka.test";
            }
        };
    }

    @AfterEach
    void tearUp() {
        sw = null;
        fieldsAndTheirTypes = null;
    }

    @Test
    void writePrivateDefaultConstructor() {
        BuilderClassGenerator generator = new BuilderClassGenerator(classAndPackageName, fieldsAndTheirTypes, new PrintWriter(sw));
        generator.writePrivateDefaultConstructor();
        String actual = sw.toString();
        assertEquals("private FooBuilder() {};\n", actual);
    }

    @Test
    void writeInit() {
        BuilderClassGenerator generator = new BuilderClassGenerator(classAndPackageName, fieldsAndTheirTypes, new PrintWriter(sw));
        generator.writeInit();
        String actual = sw.toString();
        assertThat(actual).contains("package co.xapuka.test;")
                .contains("public final class FooBuilder {")
                .contains("public static FooBuilder newInstance() {");
    }

    @Test
    void returnAndClose() {
        BuilderClassGenerator generator = new BuilderClassGenerator(null, null, null);

        String actual = generator.returnAndClose("this");

        assertThat(actual).isEqualTo("return this;\n}\n");
    }

    @Test
    void openingBracketLineBreak() {
        BuilderClassGenerator generator = new BuilderClassGenerator(null, null, null);

        String actual = generator.openingBracketLineBreak();

        assertThat(actual).isEqualTo("{\n");
    }

    @Test
    void closingBracketLineBreak() {
        BuilderClassGenerator generator = new BuilderClassGenerator(null, null, null);

        String actual = generator.closingBracketLineBreak();

        assertThat(actual).isEqualTo("}\n");
    }


    @Test
    void withBuildMethod() {
    }

    @Test
    void writeFields() {
    }

    @Test
    void writeMutators() {
    }

    @Test
    void writeEnd() {
    }

    @ParameterizedTest
    @MethodSource
    void capitalize(String value, String expected) {
        String actual = BuilderClassGenerator.capitalize(value);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void nullEdgeCaseCapitalize() {
        assertThrows(IllegalArgumentException.class, () -> BuilderClassGenerator.capitalize(null));
    }

    @Test
    void blankEdgeCaseCapitalize() {
        assertThrows(IllegalArgumentException.class, () -> BuilderClassGenerator.capitalize(""));
    }

    private static Stream<Arguments> capitalize() {
        return Stream.of(
                arguments("foo", "Foo"),
                arguments("f", "F"),
                arguments("F", "F")
        );
    }
}