package org.asuki.common.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Sample {
    public enum Level {
        LOW, STANDARD, HIGH
    };

    String value() default "";

    String[] item() default {};

    Level level() default Level.STANDARD;
}