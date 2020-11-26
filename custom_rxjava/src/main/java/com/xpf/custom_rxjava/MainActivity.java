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
}
