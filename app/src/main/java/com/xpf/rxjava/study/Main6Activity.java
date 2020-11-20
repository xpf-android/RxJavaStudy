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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import utils.Cons;

/**
 * 合并型操作符
 */
public class Main6Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

    }

    /**
     * startWith 合并操作符, 被观察者1.startWith(被观察者2) 先执行 被观察者2 里面发射的事件，然后再执行 被观察者1 发射的事件
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test1(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // todo 2
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        })
        .startWith(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // todo 1
                e.onNext(10000);
                e.onNext(20000);
                e.onNext(30000);
                e.onComplete();
            }
        }))
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(Cons.TAG, "accept: " + integer);
            }
        });
    }

    /**
     * concatWith 和 startWith 的区别，是相反的
     * concatWith 合并操作符, 被观察者1.concatWith(被观察者2) 先执行 被观察者1 里面发射的事件，然后再执行 被观察者2 发射的事件
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test2(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // todo 1
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        })
        .concatWith(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // todo 2
                e.onNext(10000);
                e.onNext(20000);
                e.onNext(30000);
                e.onComplete();
            }
        }))
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(Cons.TAG, "accept: " + integer);
            }
        });
    }

    /**
     * concat 合并操作符 的特性：最多能够合并四个，按照我们存入的顺序执行
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test3(View view) {
        // 上游 被观察者
        Observable.concat(

                Observable.just("1") // todo 1
                ,
                Observable.just("2") // todo 2
                ,
                Observable.just("3") // todo 3
                ,
                Observable.create(new ObservableOnSubscribe<String>() { // todo 4
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("4");
                        e.onComplete();
                    }
                })

        )
        .subscribe(new Consumer<String>() { // 下游 观察者
            @Override
            public void accept(String s) throws Exception {
                Log.d(Cons.TAG, "accept: " + s);
            }
        });
    }

    /**
     * merge 合并操作符 的特性：最多能够合并四个，并列执行 并发
     * @param view
     */

    @SuppressLint("CheckResult")
    public void test4(View view) {
        // 为了体现并列执行 并发，所以要新学一个操作符(intervalRange)
        // 被观察者  start开始累计， count累计多个个数量， initDelay开始等待时间，  period 每隔多久执行， TimeUnit 时间单位
        /*Observable.intervalRange(1, 5, 1,2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "accept: " + aLong);
            }
        });*/

        // 被观察者1
        Observable observable1 = Observable.intervalRange(1, 5, 1,2, TimeUnit.SECONDS);// 1 2 3 4 5
        // 被观察者2
        Observable observable2 = Observable.intervalRange(6, 5, 1,2, TimeUnit.SECONDS); // 6 7 8 9 10
        // 被观察者3
        Observable observable3 = Observable.intervalRange(11, 5, 1,2, TimeUnit.SECONDS); // 11 12 13 14 15

        // 上游
        Observable.merge(observable1, observable2, observable3) // 合并成一个 被观察者
                .subscribe(new Consumer() {
                @Override
                public void accept(Object o) throws Exception {

                    // 被观察者1  1
                    // 被观察者2  6
                    // 被观察者3  11

                    Log.d(Cons.TAG, "accept: " + o);
                }
            });
    }


    /**
     * zip 合并操作符：合并的被观察者发射的事件，需要对应
     * 需求：考试 课程 == 分数
     * @param view
     */

    public void test5(View view) {
        // 课程 被观察者
        Observable observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("英语"); // String
                e.onNext("数学");
                e.onNext("政治");
                e.onNext("物理");  // 被忽略
                e.onComplete();
            }
        });

        // 分数 被观察者
        Observable observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(85); // Integer
                e.onNext(90);
                e.onNext(96);
                e.onComplete();
            }
        });

        // zip 最多9个 被观察者
        Observable.zip(observable1, observable2, new BiFunction<String, Integer, StringBuffer>() { // T1 String, T2 Integer,  R StringBuffer
            @Override
            public StringBuffer apply(String string, Integer integer) throws Exception {
                return new StringBuffer().append("课程" + string).append("==").append(integer+"");
            }
        })
                .subscribe(/*new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "最终考试的结果 accept: " + o);
            }
        }*/

                        new Observer() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(Cons.TAG, "onSubscribe: 准备进入考场，考试了....");
                            }

                            @Override
                            public void onNext(Object o) {
                                Log.d(Cons.TAG, "onNext: 考试结果输出 " + o);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.d(Cons.TAG, "onComplete: 考试全部完毕");
                            }
                        }
                );
    }


    /**
     * 很多的数据，不想全部一起发射出去，分批次，先缓存到Buffer
     * 有间接分组的意思
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test6(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        })
        // 变换 buffer
        .buffer(30)
        .subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integer) throws Exception {
                Log.d(Cons.TAG, "accept: " + integer);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
