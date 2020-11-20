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


    /**
     * 分组变换 groupBy
     * 下游 有自己的使用标准
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test5(View view) {
        // 上游
        Observable.just(6000, 7000, 8000, 9000, 10000, 14000)

                // 变换
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer > 8000 ? "高端配置电脑" : "中端配置电脑"; // 分组
                    }
                })

                // 订阅
                /*.subscribe(new Consumer<String>() { // 下游 使用 groupBy
                    @Override
                    public void accept(String string) throws Exception {

                    }
                });*/

                // 使用groupBy下游是 有标准的
                .subscribe(new Consumer<GroupedObservable<String, Integer>>() {
                    @Override
                    public void accept(final GroupedObservable<String, Integer> groupedObservable) throws Exception {
                        Log.d(Cons.TAG, "accept: " + groupedObservable.getKey());
                        // 以上还不能把信息给打印全面，只是拿到了，分组的key

                        // 输出细节，还需要再包裹一层
                        // 细节 GroupedObservable 被观察者
                        groupedObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.d(Cons.TAG, "accept: 类别：" + groupedObservable.getKey() + "  价格：" + integer);
                            }
                        });
                    }
                });
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
