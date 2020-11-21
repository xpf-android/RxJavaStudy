package com.xpf.rxjava.study;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import utils.Cons;

/**
 * 条件操作符练习
 */
public class Main5Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

    }

    /**
     * all，如同 if 那样的功能 ：全部为true，才是true，只要有一个为false，就是false
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test1(View view) {
        String v1 = "1";
        String v2 = "2";
        String v3 = "3";
        String v4 = "cc";

        // 需求：只要有一个为 cc的，就是false

        // 平常的写法
        /*if (v1.equals("cc") || v2.equals("cc") || v3.equals("cc") || v4.equals("cc")) {
            Log.d(Cons.TAG, "test1: " + false);
        } else {
            Log.d(Cons.TAG, "test1: " + true);
        }*/

        // RxJava的写法
        // 上游
        Observable.just(v1, v2, v3, v4) // RxJava 2.X 之后 不能传递null，否则会报错

            .all(new Predicate<String>() {
                @Override
                public boolean test(String s) throws Exception {
                    return !s.equals("cc"); // 如果s不等于cc，就是true
                }
            })

            .subscribe(new Consumer<Boolean>() { // 下游
                @Override
                public void accept(Boolean s) throws Exception {
                    Log.d(Cons.TAG, "accept: " + s);
                }
            });
    }

    /**
     * contains 是否包含
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test2(View view) {
        Observable.just("JavaSE", "JavaEE", "JavaME", "Android", "iOS", "Rect.js", "NDK")

            //.contains("Android")     // 是否包含了 Android，条件是否满足 下游打印的true
            .contains("C")     // 是否包含了 C  下游打印的false

            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean s) throws Exception {
                    Log.d(Cons.TAG, "accept: " + s);
                }
            });
    }

    /**
     * Any 和 All相反的，All全部为true，才是true，只要有一个为false，就是false
     * any 全部为 false，才是false， 只要有一个为true，就是true
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test3(View view) {
        Observable.just("JavaSE", "JavaEE", "JavaME", "Android", "iOS", "Rect.js", "NDK")

                .any(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.equals("Android");
                    }
                })     // 是否包含了 Android，条件是否满足

                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean s) throws Exception {
                        Log.d(Cons.TAG, "accept: " + s);
                    }
                });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
