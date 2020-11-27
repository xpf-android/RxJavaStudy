package com.xpf.custom_rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

// TODO 我们自己写的RxJava
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    /**
     * todo 手写create操作符测试
     * @param view
     */
    public void test1(View view) {

        //上游
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(Observer<? super Integer> observableEmitter) {//泛型被使用到了(有泛型赋值的相关操作),就产生了读写模式
                //todo 使用者去调用发射 2
                Log.d(TAG, "subscribe:  上游开始发射... ");
                //发射事件可写的
                observableEmitter.onNext(5);// <? extends Integer> 不可写了  <? super Integer> 可写(不完全可读)
                observableEmitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {//下游
            //接口的实现方法
            @Override
            public void onSubscribe() {
                //todo 1
                Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
            }

            //接口的实现方法
            @Override
            public void onNext(Integer item) {
                //todo 3
                Log.d(TAG, "下游 接收处理 onNext: " + item);
            }

            //接口的实现方法
            @Override
            public void onError(Throwable e) {

            }

            //接口的实现方法
            @Override
            public void onComplete() {
                // todo 4
                Log.d(TAG, "onComplete: 下游接收事件完毕...");
            }
        });
    }

    /**
     * todo just操作符手写及测试
     * @param view
     */
    public void test2(View view) {
        //上游
        Observable.just("A","B","C","D","E","F")//todo 内部执行第2步
                //订阅
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe() {
                        //todo 1
                        Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
                    }

                    @Override
                    public void onNext(String item) {
                        //todo 3
                        Log.d(TAG, "下游 接收处理 onNext: " + item);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // todo 4
                        Log.d(TAG, "onComplete: 下游接收事件完毕...");
                    }
                });
    }

    /**
     * map操作符手写及测试
     * @param view
     */
    public void test3(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observableEmitter) { // 使用到了，就产生了读写模式
                Log.d(TAG, "subscribe: 上游开始发射...");
                // 发射事件  可写的
                // todo 使用者去调用发射 2
                observableEmitter.onNext(9); //  <? extends Integer> 不可写了   <? super Integer>可写
                observableEmitter.onComplete();
            }
        })

                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) {
                        Log.d(TAG, "第1个变换 apply: " + integer);
                        return "【" + integer + "】";
                    }
                })

                .map(new Function<String, StringBuffer>() {
                    @Override
                    public StringBuffer apply(String s) {
                        Log.d(TAG, "第2个变换 apply: " + s);
                        return new StringBuffer().append(s).append("-----------------------");
                    }
                })

                // Observable<Integer>.subscribe
                .subscribe(new Observer<StringBuffer>() { // 下游
                    // 接口的实现方法
                    @Override
                    public void onSubscribe() {
                        // todo 1
                        Log.d(TAG, "已经订阅成功，即将开始发射 onSubscribe: ");
                    }

                    // 接口的实现方法
                    @Override
                    public void onNext(StringBuffer item) {
                        // todo 3
                        Log.d(TAG, "下游接收事件 onNext: " + item); // 【9】-----------------------
                    }

                    // 接口的实现方法
                    @Override
                    public void onError(Throwable e) {

                    }

                    // 接口的实现方法
                    @Override
                    public void onComplete() {
                        // todo 4 最后一步
                        Log.d(TAG, "onComplete: 下游接收事件完成√√√√√√√√√√√√√√");
                    }
                });
    }
}
