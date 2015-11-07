package org.asuki.dp.other.dsl.fluent;

import lombok.AllArgsConstructor;

import java.beans.Statement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

@AllArgsConstructor
public final class FluentBuilder<T> implements InvocationHandler {

    private Object obj;

    @SuppressWarnings("unchecked")
    public static <T> T create(Object object, Class<T> builder) {

        return (T) Proxy.newProxyInstance(builder.getClassLoader(),
                new Class[] { builder }, new FluentBuilder<T>(object));
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        String name = method.getName();

        if ("create".equals(name)) {
            return obj;
        }

        String setter = "set" + LOWER_CAMEL.to(UPPER_CAMEL, name);

        Statement stmt = new Statement(obj, setter, args);
        stmt.execute();

        return proxy;
    }
}
