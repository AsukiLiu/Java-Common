package org.asuki.tool.guava.forwarding;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class CounterMultiset<T> extends ForwardingMultiset<T> {

    private Multiset<T> multiset;
    private Multiset<T> counter;

    public CounterMultiset(Multiset<T> multiset) {
        this.multiset = multiset;
        counter = HashMultiset.create();
    }

    @Override
    protected Multiset<T> delegate() {
        return multiset;
    }

    public int countCounter(T element) {
        return counter.count(element);
    }

    @Override
    public boolean add(T element) {
        counter.add(element);
        return super.add(element);
    }

    @Override
    public int add(T element, int occurrences) {
        counter.add(element);
        return super.add(element, occurrences);
    }

    @Override
    public void clear() {
        counter.clear();
        super.clear();
    }

}
