package org.asuki.tool.guava.forwarding;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class MaxMultiset<E> extends ForwardingMultiset<E> {

    private Multiset<E> multiset;
    private Multiset<E> max;

    public MaxMultiset(Multiset<E> multiset) {
        this.multiset = multiset;
        max = HashMultiset.create();
    }

    @Override
    protected Multiset<E> delegate() {
        return multiset;
    }

    public int countMax(E element) {
        return max.count(element);
    }

    @Override
    public boolean add(E element) {
        addElement(element, 1);
        return super.add(element);
    }

    @Override
    public int add(E element, int occurrences) {
        addElement(element, occurrences);
        return super.add(element, occurrences);
    }

    @Override
    public void clear() {
        max.clear();
        super.clear();
    }

    private void addElement(E element, int occurrences) {
        if (occurrences > max.count(element)) {
            max.setCount(element, occurrences);
        }
    }

}
