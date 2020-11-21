package com.xpf.rxjava.study;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import utils.Cons;

/**
 * 异常处理操作符，在RxJava使用的时候 的异常处理，提供操作符
 */
public class Main7Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     *
     * onErrorReturn异常操作符：1.能够接收e.onError，  2.如果接收到异常，会中断上游后续发射的所有事件
     * error
     * 注意：添加onErrorReturn，下游的onError不会收到error事件了，并且onErrorReturn内部的apply方法返回
     * 给下游的onNext
     * @param view
     */
    public void test1(View view) {
        // 上游 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        // RxJava中是不标准的, 程序直接抛异常
                        // throw new IllegalAccessError("我要报错了");

                        // RxJava标准的
                        e.onError(new IllegalAccessError("我要报错了...")); // 发射异常事件
                    }
                    e.onNext(i);
                }
                e.onComplete();
            }
        })

        // 在上游 和 下游之间 添加异常操作符
        .onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                // 处理，纪录，异常，通知给下一层
                Log.d(Cons.TAG, "onErrorReturn: " + throwable.getMessage());
                return 400; // 400代表有错误，给下一层，目前 下游 观察者
            }
        })

        .subscribe(new Observer<Integer>() { // 完整版 下游 观察者
            @Override
            public void onSubscribe(Disposable d) {

            }

            // 如果使用了 异常操作符 上游发射的onError由下游onNext接收
            @Override
            public void onNext(Integer integer) {
                Log.d(Cons.TAG, "onNext: " + integer); // 400
            }

            // 如果不使用 异常操作符 上游的发射的onError由下游的onError接收
            @Override
            public void onError(Throwable e) {
                Log.d(Cons.TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(Cons.TAG, "onComplete: ");
            }
        });
    }

    /**
     * onErrorResumeNext 异常操作符：1.能够接收e.onError，
     * onErrorReturn可以返回标识400    对比   onErrorResumeNext可以返回被观察者（被观察者可以再次发射多次事件给 下游）
     * error
     * 实际打印情况是，下游对上游连续多次发射完全相同的事件时，最多连续收到两个事件
     * @param view
     */
    public void test2(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        e.onError(new Error("错错错"));
                    } else {
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        })

        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {

                // onErrorResumeNext 返回的是 被观察者，所以再多次发射给 下游 给 观察者接收
                return Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(200);
                        e.onNext(200);
                        e.onNext(200);
                        //实际打印情况是，下游对上游连续多次发射完全相同的事件时，最多连续收到两个事件
                        e.onNext(300);
                        e.onNext(300);
                        e.onNext(300);
                        e.onNext(200);
                        e.onNext(300);
                        e.onComplete();
                    }
                });
            }
        })

        .subscribe(new Observer<Integer>() { // 下游
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(Cons.TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(Cons.TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(Cons.TAG, "onComplete: ");
            }
        });
    }

    /**
     * exception
     * onExceptionResumeNext 操作符，能在发生异常的时候，扭转乾坤，（这种错误一定是可以接受的，才这样使用）
     * 慎用：自己去考虑，是否该使用
     * @param view
     */
    public void test3(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        //throw new Exception("错了..."); //触发下游的onNext
                        e.onError(new IllegalAccessException("错了..."));//触发下游的onNext
                        //e.onError(new IllegalAccessError("错了..."));//触发下游的onError
                    } else {
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        })
        // 在上游和下游中间 增加 异常操作符
        .onExceptionResumeNext(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                observer.onNext(404); // 可以让程序 不崩溃的
            }
        })

        // 下游
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(Cons.TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(Cons.TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(Cons.TAG, "onComplete: ");
            }
        });

    }

    /**
     * retry 重试操作符 异常处理操作符中
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test4(View view) {
        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i == 5) {
                        //throw new Exception("错了...");//触发下游的onError
                        e.onError(new IllegalAccessException("错了..."));//触发下游的onError
                    } else {
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        })
        // todo 演示一
        /*.retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                Log.d(TAG, "retry: " + throwable.getMessage());
                // return false; // 代表不去重试
                return true; // 一直重试，不停的重试
            }
        })*/

        // todo 演示二 重试次数
        /*.retry(3, new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                Log.d(Cons.TAG, "retry: " + throwable.getMessage());
                return true;
            }
        })*/

        // todo 演示三 打印重试了多少次，计数     Throwable  +  count
        .retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer integer, Throwable throwable) throws Exception {
                Thread.sleep(2);
                Log.d(Cons.TAG, "retry: 已经重试了:" + integer + "次  e：" + throwable.getMessage());
                return true; // 重试
            }
        })

        //订阅
        .subscribe(
                // 下游
                new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(Cons.TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(Cons.TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Cons.TAG, "onComplete: ");
                    }
                });
    }
}
