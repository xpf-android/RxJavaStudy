package com.xpf.rxjava.study;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import utils.Cons;

public class Main8Activity extends AppCompatActivity {

    private final String PATH = "http://pic33.nipic.com/20131007/13639685_123501617185_2.jpg";

    private ImageView imageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        Log.d(Cons.TAG, "Main8Activity onCreate: " + Thread.currentThread().getName());
    }


    /**
     * todo 异步线程区域
     * Schedulers.io() ：代表io流操作，网络操作，文件流，耗时操作
     * Schedulers.newThread()    ： 比较常规的，普普通通
     * Schedulers.computation()  ： 代表CPU 大量计算 所需要的线程
     *
     * todo main线程 主线程
     * AndroidSchedulers.mainThread()  ： 专门为Android main线程量身定做的
     *
     * todo 给上游分配多次，只会在第一次切换，后面的不切换了（忽略）
     * todo 给下游分配多次，每次都会去切换
     *
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test1(View view) {
        // RxJava如果不配置，默认就是主线程main
        // 上游
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(Cons.TAG, "上游 subscribe: " + Thread.currentThread().getName());
                e.onNext("");
            }
        })
        .subscribeOn(Schedulers.io())//todo 给上游配置异步线程    // 给上游分配多次，只会在第一次切换，后面的不切换了
            .subscribeOn(Schedulers.newThread())//被忽略
            .subscribeOn(Schedulers.io())//被忽略
        .observeOn(AndroidSchedulers.mainThread())//todo 给下游配置 主线程 // 给下游分配多次，每次都会去切换
            .observeOn(Schedulers.newThread())//切换一次线程
            .observeOn(AndroidSchedulers.mainThread())//切换一次线程
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String string) throws Exception {
                Log.d(Cons.TAG, "下游 accept: " + string);
                Log.d(Cons.TAG, "下游 accept: " + Thread.currentThread().getName());
            }
        });
    }

    /**
     * todo 同步 和 异步 执行流程
     * @param view
     */
    @SuppressLint("CheckResult")
    public void test2(View view) {
        // TODO 默认情况下  上游和下游都是main线程的情况下
        //上游
        /*Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(Cons.TAG, "subscribe: 上游发送了一次 1 ");
                e.onNext(1);

                Log.d(Cons.TAG, "subscribe: 上游发送了一次 2 ");
                e.onNext(2);

                Log.d(Cons.TAG, "subscribe: 上游发送了一次 3 ");
                e.onNext(3);
            }
        })
        //订阅
        .subscribe(
                //下游简化版
                new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(Cons.TAG, "下游 accept: " + integer );
                    }
        });*/

        /*
        默认情况下，同步的想象
        com.xpf.rxjava.study D/xpf: subscribe: 上游发送了一次 1
        com.xpf.rxjava.study D/xpf: 下游 accept: 1
        com.xpf.rxjava.study D/xpf: subscribe: 上游发送了一次 2
        com.xpf.rxjava.study D/xpf: 下游 accept: 2
        com.xpf.rxjava.study D/xpf: subscribe: 上游发送了一次 3
        com.xpf.rxjava.study D/xpf: 下游 accept: 3
        */

        //TODO 配置好异步线程
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(Cons.TAG, "subscribe: 上游发送了一次 1 ");
                e.onNext(1);

                Log.d(Cons.TAG, "subscribe: 上游发送了一次 2 ");
                e.onNext(2);

                Log.d(Cons.TAG, "subscribe: 上游发送了一次 3 ");
                e.onNext(3);
            }
        })
        .subscribeOn(Schedulers.io())//给上游配置异步线程
        .observeOn(AndroidSchedulers.mainThread())//给下游配置主线程
        .subscribe(//下游简化版
                new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(Cons.TAG, "下游 accept: " + integer);
                    }
                });


        /*配置了 异步后
        com.xpf.rxjava.study D/xpf: subscribe: 上游发送了一次 1
        com.xpf.rxjava.study D/xpf: subscribe: 上游发送了一次 2
        com.xpf.rxjava.study D/xpf: subscribe: 上游发送了一次 3
        com.xpf.rxjava.study D/xpf: 下游 accept: 1
        com.xpf.rxjava.study D/xpf: 下游 accept: 2
        com.xpf.rxjava.study D/xpf: 下游 accept: 3
        */
    }

    /**
     * todo 不使用RxJava去下载图片
     * @param view
     */
    public void test3(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在下载中...");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);

                    URL url = new URL(PATH);
                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.setConnectTimeout(5000);
                    int responseCode = httpURLConnection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == responseCode) {
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        Message message = handler.obtainMessage();
                        message.obj = bitmap;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            imageView.setImageBitmap(bitmap);

            // 隐藏加载框
            if (progressDialog != null)
                progressDialog.dismiss();
            return false;
        }
    });


    /**
     * todo 使用RxJava去下载图片
     * @param view
     */
    public void test4(View view) {
        //上游 被观察者 Observable
        Observable.just(PATH)//内部发射事件
        // String path 变换 Bitmap
        .map(new Function<String, Bitmap>() {
            @Override
            public Bitmap apply(String s) throws Exception {
                try {
                    Thread.sleep(2000);

                    URL url = new URL(PATH);
                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.setConnectTimeout(5000);
                    int responseCode = httpURLConnection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == responseCode) {
                        Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                        return bitmap;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        })

        .map(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) throws Exception {
                // 给图片加水印
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setTextSize(30);
                Bitmap bitmapSuccess = drawTextToBitmap(bitmap, "风景这边独好", paint, 400, 60);
                return bitmapSuccess;
            }
        })

        // 比如：增加一个 日志纪录功能，只需要添加要给 变换操作符
        .map(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) throws Exception {
                Log.d(Cons.TAG, "apply: 下载的Bitmap 是这个样子的" + bitmap);
                return bitmap;
            }
        })

        .subscribeOn(Schedulers.io())//给上游配置异步线程
        .observeOn(AndroidSchedulers.mainThread())//给下游配置主线程
        .subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
                progressDialog = new ProgressDialog(Main8Activity.this);
                progressDialog.setMessage("RxJava异步下载中...");
                progressDialog.show();
            }

            @Override
            public void onNext(Bitmap bitmap) {
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onError(Throwable e) {
                //  if (imageView != null)
                //  imageView.setImageResource(R.mipmap.ic_launcher); // 下载错误的图片
            }

            @Override
            public void onComplete() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });

    }

    //图片上绘制文字
    private Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }
}
