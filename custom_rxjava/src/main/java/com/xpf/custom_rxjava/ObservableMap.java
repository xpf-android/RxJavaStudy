package com.xpf.custom_rxjava;

public class ObservableMap<T,R> implements ObservableOnSubscribe<R> {

    private ObservableOnSubscribe<T> source;//通过它具有控制上一层的能力
    private Function<? super T, ? extends R> function;
    private Observer<? super R> observableEmitter;//通过它具有控制下一层的能力


    public  ObservableMap(ObservableOnSubscribe source, Function<? super T,? extends R> function) {
        this.source = source;
        this.function = function;
    }

    @Override
    public void subscribe(Observer<? super R> observableEmitter) {
        this.observableEmitter = observableEmitter;
        //source.subscribe(observableEmitter);//不应该把下一层的Observer交出去--->上一层，如果交出去了，map没有控制权,不能继续.map 继续变换
        //source.subscribe(observableEmitter);
        MapObserver<T> mapObserver = new MapObserver(observableEmitter, source, function);

        source.subscribe(mapObserver);//把我们自己map 交出去了
    }

    //真正拥有控制下一层的能力 让map拥有控制权力 observer,source,function
    class MapObserver<T> implements Observer<T> {

        private Observer<R> observableEmitter;//传递给下一层的类型，意思是变换后的类型 R
        private ObservableOnSubscribe<T> source;//通过它具有控制上一层的能力
        private Function<? super T, ? extends R> function;


        public MapObserver(Observer<R> observableEmitter,
                           ObservableOnSubscribe<T> source,
                           Function<? super T,? extends R> function) {
            this.observableEmitter = observableEmitter;
            this.source = source;
            this.function = function;
        }

        @Override
        public void onSubscribe() {
            observableEmitter.onSubscribe();
        }

        @Override
        public void onNext(T item) {//真正做变换的操作
            /**
             * T Integer   变换    R String
             */
            R nextMapResultSuccessType = function.apply(item);
            //调用下一层的onNext方法
            observableEmitter.onNext(nextMapResultSuccessType);
        }

        @Override
        public void onError(Throwable e) {
            observableEmitter.onError(e);
        }

        @Override
        public void onComplete() {
            observableEmitter.onComplete();
        }
    }
}
