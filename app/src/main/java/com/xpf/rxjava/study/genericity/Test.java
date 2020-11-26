package com.xpf.rxjava.study.genericity;

public class Test<T> {

    private T t;

    public void add(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

}
