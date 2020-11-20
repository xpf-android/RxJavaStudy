package com.xpf.rxjava.study;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import utils.Cons;

/**
 * 过滤操作符练习
 */
public class Main4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

    }

    /**
     * filter 过滤
     * 需求：过滤掉 那些不合格的奶粉，输出那些合格的奶粉
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test1(View view) {
        // 上游
        Observable.just("三鹿", "合生元", "飞鹤")
                //过滤操作
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        // return true; // 不去过滤，默认全部都会打印
                        // return false; // 如果是false 就全部都不会打印
                        if ("三鹿".equals(s)) {
                            return false; // 不合格
                        }
                        return true;
                    }
                })
                // 订阅
                .subscribe(new Consumer<String>() { // 下游
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(Cons.TAG, "accept: " + s);
                    }
                });
    }

    /**
     * take过滤操作符
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test2(View view) {
        // 定时器 运行   只有在定时器运行基础上 加入take过滤操作符，才有take过滤操作符的价值

        // 上游
        Observable.interval(2, TimeUnit.SECONDS)

                // 增加过滤操作符，停止定时器
                .take(8) // 执行次数达到8 停止下来

                .subscribe(new Consumer<Long>() { // 下游
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(Cons.TAG, "accept: " + aLong);
                    }
                });
    }

    /**
     * distinct过滤重复事件
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test3(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(4);
                e.onComplete();
            }
        })
        .distinct() // 过滤重复 发射的事件
        .subscribe(new Consumer<Integer>() { // 下游 观察者
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(Cons.TAG, "accept: " + integer); // 事件不重复
            }
        });
    }

    /**
     * elementAl 指定过滤的内容
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test4(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("九阴真经");
                e.onNext("九阳真经");
                e.onNext("易筋经");
                e.onNext("神照经");
                e.onComplete();
            }
        })

        // 过滤操作符
        //.elementAt(2, "默认经") // 指定下标输出 事件 下游打印 易筋经
        .elementAt(100, "默认经") // 指定下标输出 事件 下游打印 默认经

        // 订阅
        .subscribe(new Consumer<String>() { // 下游
            @Override
            public void accept(String s) throws Exception {
                Log.d(Cons.TAG, "accept: " + s);
            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
