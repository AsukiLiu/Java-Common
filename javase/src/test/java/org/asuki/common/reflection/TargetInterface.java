package org.asuki.common.reflection;

public interface TargetInterface {
    String publicMethod(Number num);

    default String defaultMethod() {
        return "default method";
    };
}
