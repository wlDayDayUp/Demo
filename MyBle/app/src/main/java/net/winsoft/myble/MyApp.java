package net.winsoft.myble;

import android.app.Application;

import com.blankj.utilcode.util.Utils;


public class MyApp extends Application {

    private static MyApp instance;
    private final static String TAG = "wg";


    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Utils.init(this);

    }
}