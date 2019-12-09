package com.wl1217.cjsw;

import android.app.Application;


import java.util.concurrent.TimeUnit;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import rxhttp.wrapper.param.RxHttp;

public class MyApp extends Application {

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initRxHttp();   /* 初始化网络请求 */
    }

    /**
     * 初始化网络请求
     */
    private void initRxHttp() {

        //设置RxJava 全局异常处理
        RxJavaPlugins.setErrorHandler(throwable -> {
            /*
              RxJava2的一个重要的设计理念是：不吃掉任何一个异常,即抛出的异常无人处理，便会导致程序崩溃
              这就会导致一个问题，当RxJava2“downStream”取消订阅后，“upStream”仍有可能抛出异常，
              这时由于已经取消订阅，“downStream”无法处理异常，此时的异常无人处理，便会导致程序崩溃
             */
        });
        RxHttp.setDebug(BuildConfig.DEBUG);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        RxHttp.init(builder.build());
    }
}