package com.xpf.custom_rxjava;

//被观察者 上游
public class Observable<T> {//类声明的泛型T

    ObservableOnSubscribe source;

    private Observable(ObservableOnSubscribe source) {
        this.source = source;
    }

    //创建型操作符create
    //静态方法声明的<T>泛型                 ObservableOnSubscribe<T>==静态方法声明的<T>泛型
    //参数中：ObservableOnSubscribe<? extends T> 和可读可写模式没有任何关系，还是通配符上下限的问题
    public static <T> Observable<T> create(ObservableOnSubscribe<? extends T> source) {
        return new Observable<T>(source);//静态方法声明的<T>泛型 int
    }

    //创建型型操作符
    //just 可变参数
    public static <T> Observable<T> just(final T... t) {//just操作符内部执行发射操作
        //想办法让source不为null，而create操作符是使用者自己传进来的
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {
                for (T t1 : t) {
                    //发射用户传递的参数数据，去发射事件
                    observableEmitter.onNext(t1);
                }
                //调用完毕
                observableEmitter.onComplete();//发射事件完毕
            }
        });
    }

    //just操作符 单一参数
    public static <T> Observable<T> just(final T t) {//just操作符内部执行发射操作
        //想办法让source不为null，而create操作符是使用者自己传进来的
        return new Observable<T>(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(Observer<? super T> observableEmitter) {

                //发射用户传递的参数数据，去发射事件
                observableEmitter.onNext(t);

                //调用完毕
                observableEmitter.onComplete();//发射事件完毕
            }
        });
    }

    /**
     * 变换型操作符map
     * T == 上一层传递过来的类型 Integer 变换前的类型
     * R == 去传递给下一层的类型 String  变换后的类型
     * @return
     */
    public <R> Observable<R> map(Function<? super T, ? extends R> function) {//<? super T, R>可写模式

        ObservableMap<T, R> observableMap = new ObservableMap(source, function);//source 上一层的能力

        return new Observable<R>(observableMap);//source 该怎么来？ observableMap是source的实现类
    }


    public void subscribe(Observer<? extends T> observer) {
        //这就是为什么一旦订阅onSubscribe最先执行。
        observer.onSubscribe();
        //然后将observer
        source.subscribe(observer);
    }


}
