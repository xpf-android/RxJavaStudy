package com.xpf.rxjava.study;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import utils.Cons;

public class MainActivity extends AppCompatActivity {

    Disposable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void test1(View view) {
        //起点  被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

            }
        }).subscribe(
                //终点 观察者
                new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

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
     * 角色拆分书写
     * @param view
     */
    public void test2(View view) {
        //TODO 上游 Observable 被观察者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            // ObservableEmitter<Integer> emitter 发射器 发射事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //上游发射事件
                Log.d(Cons.TAG, "上游subscribe: 发射事件");
                emitter.onNext(1);
                Log.d(Cons.TAG, "上游subscribe: 发射完成");
            }
        });


        //TODO 下游 Observer 观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(Cons.TAG, "下游 接收处理onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        //TODO 被观察者(上游) 订阅  观察者(下游)
        observable.subscribe(observer);
    }

    /**
     * 链式调用
     * @param view
     */
    public void test3(View view) {
        //TODO 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //上游发射事件
                Log.d(Cons.TAG, "上游subscribe: 发射事件");
                emitter.onNext(2);
                Log.d(Cons.TAG, "上游subscribe: 发射完成");
            }
        })
        //订阅操作
        .subscribe(
            //TODO 下游 Observer 观察者
            new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Integer integer) {
                    Log.d(Cons.TAG, "下游 接收处理onNext: " + integer);
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
     * 事件流程1
     * @param view
     */
    public void test4(View view) {
        //TODO 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //上游发射事件
                Log.d(Cons.TAG, "上游 subscribe: 发射事件");//TODO 2
                emitter.onNext("4");
                //emitter.onComplete();//发射完成 //TODO 4 调用才会触发5，否则不会
                Log.d(Cons.TAG, "上游 subscribe: 发射完成");// TODO 6 先接收完成，整个发射事件才算完成
            }
        })
        //订阅操作
        .subscribe(
            //TODO 下游 Observer 观察者
            new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {//TODO 1
                    Log.d(Cons.TAG, "上游和下游 onSubscribe: 订阅成功...");
                }

                @Override
                public void onNext(String string) {//TODO 3
                    Log.d(Cons.TAG, "下游 接收处理onNext: " + string);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {//TODO 5
                    Log.d(Cons.TAG, "下游 onComplete: 接收完成");
                }
            });
    }


    /**
     * 事件流程2
     * @param view
     */
    public void test5(View view) {
        //TODO 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //上游发射事件
                Log.d(Cons.TAG, "上游 subscribe: 发射事件");//TODO 2
                emitter.onNext("4");
                //emitter.onComplete();//发射完成 //TODO 4 调用才会触发5，否则不会
                Log.d(Cons.TAG, "上游 subscribe: 发射完成");// TODO 6 先接收完成，整个发射事件才算完成

                //emitter.onError(new IllegalAccessException("RxJava error..."));

                //TODO 结论： 在emitter.onComplete()/onError() 之后，上游再次发射事件 下游不再接收上游的事件
                /*emitter.onNext("a");
                emitter.onNext("b");
                emitter.onNext("c");*/

                //TODO 结论：已经发射了onComplete(); 再发送onError(), 会报错
                /*emitter.onComplete();
                emitter.onError(new IllegalAccessException("RxJava error..."));*/

                //TODO 结论：先发送onError(); 再发射onComplete(); 不会报错，但是下游的onComplete方法不会触发
                emitter.onError(new IllegalAccessException("RxJava error..."));
                emitter.onComplete();

            }
        })
        //订阅操作
        .subscribe(
            //TODO 下游 Observer 观察者
            new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {//TODO 1
                    //弹出加载框...
                    Log.d(Cons.TAG, "上游和下游 onSubscribe: 订阅成功...");
                }

                @Override
                public void onNext(String string) {//TODO 3
                    Log.d(Cons.TAG, "下游 接收处理onNext: " + string);
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(Cons.TAG, "下游接收 onError: " + e.getMessage());
                }

                @Override
                public void onComplete() {//TODO 5
                    //隐藏加载框...
                    Log.d(Cons.TAG, "下游 onComplete: 接收完成");
                }
            });
    }


    /**
     * 切断下游，让下游不再接收上游的事件
     * 实际应用中的场景之一就是不去更新UI
     * @param view
     */
    public void test6(View view) {
        //TODO 上游 Observable 被观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //上游发射事件
                for (int i = 0; i < 100; i++) {
                    emitter.onNext(i);
                }
            }
        })
        //订阅操作
        .subscribe(
            //TODO 下游 Observer 观察者
            new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {
                    MainActivity.this.d = d;
                }

                @Override
                public void onNext(Integer integer) {
                    Log.d(Cons.TAG, "下游 接收处理onNext: " + integer);
                    //接收一个事件之后，就切断下游，让下游不再接收事件,此时上游仍然在发射事件
                    //结合实际情况，一般在onDestroy方法执行该操作比较合理
                    //d.dispose();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //切断下游
        d.dispose();
    }
}
