package com.xpf.custom_rxjava;

//观察者 下游
public interface Observer<T> {

    public void onSubscribe();
    public void onNext(T item);
    public void onError(Throwable e);
    public void onComplete();
}
