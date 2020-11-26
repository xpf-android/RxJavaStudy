package com.xpf.custom_rxjava;




public interface ObservableOnSubscribe<T> {

    // ？super 代表可写的
    public void subscribe(Observer<? super T> observableEmitter);
}
