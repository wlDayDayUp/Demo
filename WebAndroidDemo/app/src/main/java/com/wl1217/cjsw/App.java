package com.wl1217.cjsw;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import java.util.concurrent.TimeUnit;

import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import rxhttp.wrapper.param.RxHttp;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);


            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);

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
