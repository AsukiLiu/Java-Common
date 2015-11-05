package org.asuki.common.model;

import org.asuki.common.annotation.HintsInherited;

@HintsInherited
public class Super {
    private int superPrivate;

    public int superPublic;

    public Super() {
    }

    private int superPrivate() {
        return superPrivate;
    }

    public int superPublice() {
        return superPrivate();
    }
}
