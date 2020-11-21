package com.xpf.rxjava.study;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import utils.Cons;
//创建型操作符
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * create 操作符
     * 创建Observable 被观察者
     * @param view
     */
    public void test1(View view) {
        //TODO 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //上游发射事件
                emitter.onNext("A");//使用者手动发射
            }
        })
        //订阅操作
        .subscribe(
            //TODO 下游 Observer 观察者
            new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(String string) {
                    Log.d(Cons.TAG, "下游 接收处理onNext: " + string);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
    }

    /**
     * just 操作符 参数个数可变，但不能为数组
     * 创建Observable 被观察者
     * @param view
     */
    public void test2(View view) {
        //TODO 上游 Observable 被观察者
        Observable.just("A","B")//内部会先发射A，再发射B
                //订阅操作
                .subscribe(
                        //TODO 下游 Observer 观察者
                        new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(String string) {
                                Log.d(Cons.TAG, "下游 接收处理onNext: " + string);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
    }

    /**
     * fromArray 操作符 参数可以为数组
     * 创建Observable 被观察者
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test3(View view) {
        /*String[] strings = {"1","2","3"};
        //TODO 上游 Observable 被观察者
        Observable.fromArray(strings)//内部会先发射A，再发射B
                //订阅操作
                .subscribe(
                        //TODO 下游 Observer 观察者
                        new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(String string) {
                                Log.d(Cons.TAG, "下游 接收处理onNext: " + string);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });*/

        String[] strings = {"张三", "李四", "王五"};

        // for
        for (String string : strings) {
            Log.d(Cons.TAG, "test3: " + string);
        }

        Log.d(Cons.TAG, "test3: ----- ");

        // RxJava
        Observable.fromArray(strings)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(Cons.TAG, "accept: " + s);
                    }
                });
    }

    /**
     * 为什么只支持Object ？
     * 上游没有发射有值得事件，下游无法确定类型，默认Object，RxJava泛型 泛型默认类型==Object
     *
     * 做一个耗时操作，不需要任何数据来刷新UI， empty的使用场景之一
     *
     * @param view
     */
    public void test4(View view) {
        // 上游无法指定 事件类型
        Observable.empty() // 内部一定会只调用 发射 onComplete 完毕事件
                // 订阅
                .subscribe(
                        // 下游 观察者
                        new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Object integer) {
                                // 没有事件可以接受
                                Log.d(Cons.TAG, "onNext: " + integer);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.d(Cons.TAG, "onComplete: ");

                                // 隐藏 加载框...
                            }
                        }

                        /*// 简化版 观察者
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                // 接受不到
                                // 没有事件可以接受
                                Log.d(TAG, "accept: " + o);
                            }
                        }*/

                );
    }

    public void test5(View view){

        // 上游  range内部会去发射
        // Observable.range(1, 8) // 1 2 3 4 5 6 7 8  从1开始加 数量共8个
        Observable.range(80, 5) // 80开始  80 81 82 83 84  加    数量共5个

                // 订阅
                .subscribe(

                        // 下游
                        new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.d(Cons.TAG, "accept: " + integer);
                            }
                        });

    }
}
