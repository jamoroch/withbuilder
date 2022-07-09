package co.xapuka.withbuilder;

import javax.annotation.processing.AbstractProcessor;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
/**
 * Provides a class with a code-generated object builder
 */
public @interface WithBuilder {
    String suffix() default "Builder";
}
