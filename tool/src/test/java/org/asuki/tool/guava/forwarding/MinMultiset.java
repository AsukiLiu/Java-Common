package org.asuki.tool.guava.forwarding;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class MinMultiset<E> extends ForwardingMultiset<E> {

    private Multiset<E> multiset;
    private Multiset<E> min;

    public MinMultiset(Multiset<E> multiset) {
        this.multiset = multiset;
        min = HashMultiset.create();
    }

    @Override
    protected Multiset<E> delegate() {
        return multiset;
    }

    public int countMin(E element) {
        return min.count(element);
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
        min.clear();
        super.clear();
    }

    private void addElement(E element, int occurrences) {
        if (occurrences < min.count(element) || !min.contains(element)) {
            min.setCount(element, occurrences);
        }
    }

}
