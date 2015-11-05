package org.asuki.tool.guava.forwarding;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingList;

public class MovableList<E> extends ForwardingList<E> {

    private final Predicate<Integer> CHECK_INDEX = new Predicate<Integer>() {
        @Override
        public boolean apply(Integer index) {
            return index < size() && index >= 0;
        }
    };

    private List<E> delegate;

    public MovableList(List<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<E> delegate() {
        return delegate;
    }

    public boolean moveUp(int index) {
        checkArgument(CHECK_INDEX.apply(index));

        if (index == 0) {
            return false;
        }

        move(index, index - 1);
        return true;
    }

    public boolean moveDown(int index) {
        checkArgument(CHECK_INDEX.apply(index));

        if (index == size() - 1) {
            return false;
        }

        move(index, index + 1);
        return true;
    }

    public boolean moveTop(int index) {
        checkArgument(CHECK_INDEX.apply(index));

        if (index == 0) {
            return false;
        }

        move(index, 0);
        return true;
    }

    public boolean moveBottom(int index) {
        checkArgument(CHECK_INDEX.apply(index));

        if (index == size() - 1) {
            return false;
        }

        move(index, size() - 1);
        return true;
    }

    private void move(int from, int to) {
        E element = remove(from);
        add(to, element);
    }

}
