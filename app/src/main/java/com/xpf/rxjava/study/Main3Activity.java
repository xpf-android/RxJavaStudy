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
import io.reactivex.observables.GroupedObservable;
import utils.Cons;

/**
 * 变换操作符练习
 */
public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * map 变换 操作符
     * @param view
     */
    public void test1(View view) {
        // 上游
        Observable.just(1) // 发射 1

                // 在上游和下游之间 变换
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        // 1
                        Log.d(Cons.TAG, "map1 apply: " + integer);

                        return "【" + integer + "】";
                    }
                })

                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        // s == 【" + integer + "】
                        Log.d(Cons.TAG, "map2 apply: " + s);
                        return Bitmap.createBitmap(1920, 1280, Bitmap.Config.ARGB_8888);

                        //return null; // 如果返回null，下游无法接收
                    }
                })

                // 订阅
                .subscribe(

                        // 下游
                        new Observer<Bitmap>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Bitmap string) {
                                Log.d(Cons.TAG, "下游 onNext: " + string);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        }

                );
    }

    /**
     * flatMap 变换 操作符
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test2(View view) {
        // 上游
        Observable.just(111)
            // 变换操作符
            .flatMap(new Function<Integer, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(final Integer integer) throws Exception {

                    // integer == 111


                    // ObservableSource == 可以再次手动发送事件
                    return Observable.create(new ObservableOnSubscribe<String>() {
                        @Override
                        public void subscribe(ObservableEmitter<String> e) throws Exception {

                            /*e.onNext(integer + " flatMap变换操作符");
                            e.onNext(integer + " flatMap变换操作符");
                            e.onNext(integer + " flatMap变换操作符");*/
                            //实际运行结果：如果是重复多次发射完全相同的事件，并不一定每次都会被下游接收
                            //完全相同的事件多次重复发射，下游默认接收两次
                            e.onNext(integer + " flatMap变换操作符 1");
                            e.onNext(integer + " flatMap变换操作符 2");
                            e.onNext(integer + " flatMap变换操作符 3");
                        }
                    });
                }
            })
            // 订阅
            .subscribe(
                    // 下游
                    new Consumer<String>() {
                        @Override
                        public void accept(String string) throws Exception {
                            Log.d(Cons.TAG, "下游接收 变换操作符 发射的事件 accept: " + string);
                        }
                    });
    }

    /**
     * 体现 flatMap 变换 操作符 是不排序的
     * @param view
     */
    public void test3(View view) {
        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("步惊云"); // String
                e.onNext("雄霸");
                e.onNext("李四");
                e.onComplete();
            }
        })
        .flatMap(new Function<String, ObservableSource<?>>() { // ? 通配符 默认Object, 下游默认接收Object类型
            @Override
            public ObservableSource<?> apply(String s) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(s + " 下标：" + (1 + i));
                }
                return Observable.fromIterable(list).delay(5, TimeUnit.SECONDS); // 创建型操作符，创建被观察者
            }
        })
        // 订阅
        .subscribe(
                /*new Consumer<Object>() { // 下游
                    @Override
                    public void accept(Object s) throws Exception {
                        Log.d(TAG, "下游 accept: " + s);
                    }
                  }*/

                new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(Cons.TAG, "下游 onNext: " + o);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }

        );
    }

    /**
     * 体现 concatMap 变换操作符 排序的
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test4(View view) {
        // 上游
        Observable.just("A", "B", "C")
                // 变换操作符
                .concatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add(s + " 下标：" + (1 + i));
                        }
                        return Observable.fromIterable(list).delay(5, TimeUnit.SECONDS); // 创建型操作符，创建被观察者
                    }
                })
                //订阅
                .subscribe(
                        new Consumer<Object>() { // 下游
                            @Override
                            public void accept(Object obj) throws Exception {
                                Log.d(Cons.TAG, "accept: " + obj);
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
