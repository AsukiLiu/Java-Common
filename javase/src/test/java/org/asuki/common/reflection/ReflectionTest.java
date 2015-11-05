package org.asuki.common.reflection;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.testng.annotations.Test;

public class ReflectionTest {

    @Test
    public void shouldInvokeInstanceMethod() throws Throwable {
        MethodHandle sayHelloHandle = MethodHandles.lookup().findVirtual(
                TargetClass.class, "sayHello",
                MethodType.methodType(String.class, String.class));

        MethodHandle boundHandle = sayHelloHandle.bindTo(new TargetClass());

        assertThat(boundHandle.invokeWithArguments("Jack"), is("Hello Jack"));
    }

    @Test
    public void shouldInvokeDefaultMethod() {
        TargetInterface target = (TargetInterface) Proxy.newProxyInstance(
                        TargetInterface.class.getClassLoader(),
                        new Class[] { TargetInterface.class },
                        (proxy, method, args) -> {

                            // @formatter:off
                            if (method.isDefault()) {
                                final Class<?> declaringClass = method.getDeclaringClass();
                                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                                        .getDeclaredConstructor(Class.class, int.class);
                                constructor.setAccessible(true);

                                return constructor
                                        .newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                                        .unreflectSpecial(method, declaringClass)
                                        .bindTo(proxy)
                                        .invokeWithArguments(args);
                            }
                            // @formatter:on

                            out.println(format("%s%s executed",
                                    method.getName(), Arrays.toString(args)));
                            return "public method";
                        });

        assertThat(target.publicMethod(10), is("public method"));
        assertThat(target.defaultMethod(), is("default method"));
    }

}

class TargetClass {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
