package org.asuki.common.model;

import org.asuki.common.annotation.Hints;

@Hints
public class Sub extends Super {
    private int subPrivate;

    public int subPublic;

    private Sub() {
    }

    public Sub(int i) {
        this();
    }

    private int subPrivate() {
        return subPrivate;
    }

    public int subPublice() {
        return subPrivate();
    }
}
