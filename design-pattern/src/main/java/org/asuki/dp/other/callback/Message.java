package org.asuki.dp.other.callback;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.asuki.dp.other.callback.MessageType.NORMAL;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Message {
    MessageType type() default NORMAL;
}
