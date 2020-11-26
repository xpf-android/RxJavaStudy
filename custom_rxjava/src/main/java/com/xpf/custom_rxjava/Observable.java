package com.xpf.custom_rxjava;

//被观察者 上游
public class Observable<T> {//类声明的泛型T

    ObservableOnSubscribe source;

    private Observable(ObservableOnSubscribe source) {
        this.source = source;
    }

    //静态方法声明的<T>泛型                 ObservableOnSubscribe<T>==静态方法声明的<T>泛型
    //参数中：ObservableOnSubscribe<? extends T> 和可读可写模式没有任何关系，还是通配符上下限的问题
    public static <T> Observable<T> create(ObservableOnSubscribe<? extends T> source) {
        return new Observable<T>(source);
    }

    public void subscribe(Observer<? extends T> observer) {
        observer.onSubscribe();

        source.subscribe(observer);
    }


}
