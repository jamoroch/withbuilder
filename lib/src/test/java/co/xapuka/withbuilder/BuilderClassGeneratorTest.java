package co.xapuka.withbuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void capitalize() {
    }
}