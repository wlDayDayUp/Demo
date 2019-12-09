package com.wl1217.cjsw;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.just.agentwebX5.AgentWebX5;
import com.just.agentwebX5.ChromeClientCallbackManager;
import com.just.agentwebX5.DefaultWebClient;
import com.just.agentwebX5.DownLoadResultListener;
import com.just.agentwebX5.LogUtils;
import com.just.agentwebX5.MiddleWareWebChromeBase;
import com.just.agentwebX5.MiddleWareWebClientBase;
import com.just.agentwebX5.PermissionInterceptor;
import com.just.agentwebX5.WebDefaultSettingsManager;
import com.just.agentwebX5.WebSettings;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;

/**
 * Created by cenxiaozhong on 2017/5/26.
 */

public class BaseWebActivity extends AppCompatActivity {

    private static final String TAG = "wl";
    protected AgentWebX5 mAgentWebX5;
    private LinearLayout mLinearLayout;
    //    private Toolbar mToolbar;
//    private TextView mTitleTextView;
    private AlertDialog mAlertDialog;
    private MiddleWareWebChromeBase mMiddleWareWebChrome;
    private MiddleWareWebClientBase mMiddleWareWebClient;

    private double lat;
    private double lng;
    public LocationClient mLocationClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);

        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setOpenGps(true); // 打开gps
        mOption.setCoorType("bd09ll");
        mOption.setScanSpan(3000);
        mOption.setIsNeedAddress(true);
        mOption.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(mOption);//设置定位参数
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.d("wg", "onReceiveLocation: " + bdLocation.getLatitude());
                Log.d("wg", "onReceiveLocation: " + bdLocation.getLongitude());
                lat = bdLocation.getLatitude();
                lng = bdLocation.getLongitude();
            }
        });


        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
//        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        mToolbar.setTitleTextColor(Color.WHITE);
//        mToolbar.setTitle("");
//        mTitleTextView = (TextView) this.findViewById(R.id.toolbar_title);
//        this.setSupportActionBar(mToolbar);
//        if (getSupportActionBar() != null)
//            // Enable the Up button
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showDialog();
//            }
//        });

        mAgentWebX5 = AgentWebX5.with(this)//
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .setWebSettings(getSettings())//
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                .setReceivedTitleCallback(mCallback)
                .setPermissionInterceptor(mPermissionInterceptor)
                .setNotifyIcon(R.mipmap.download)
                .useMiddleWareWebChrome(getMiddleWareWebChrome())
                .useMiddleWareWebClient(getMiddleWareWebClient())
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .setSecutityType(AgentWebX5.SecurityType.strict)
                .interceptUnkownScheme()
                .openParallelDownload()
                .addDownLoadResultListener(mDownLoadResultListener)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());
        mAgentWebX5.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWebX5, this));
//        WebView webView = mAgentWebX5.getWebCreator().get();
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setDatabaseEnabled(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
//
//        mAgentWebX5.getWebSettings().toSetting(webView);
    }

    public class AndroidInterface {
        private Handler deliver = new Handler(Looper.getMainLooper());
        private AgentWebX5 agent;
        private Context context;

        public AndroidInterface(AgentWebX5 agent, Context context) {
            this.agent = agent;
            this.context = context;
        }

        @JavascriptInterface
        public void callAndroidOpen() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    if (context != null) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hasPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                            int hasPermission1 = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                            if (hasPermission == PackageManager.PERMISSION_GRANTED || hasPermission1 == PackageManager.PERMISSION_GRANTED) {

                                if (isLocServiceEnable(context)) {
                                    if (mLocationClient != null) {
                                        if (!mLocationClient.isStarted()) {
                                            mLocationClient.start();
                                            Log.d(TAG, "run: " + "开启定位");
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "请打开GPS开关", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(context, "请打开定位权限", Toast.LENGTH_SHORT).show();

                                new AlertDialog.Builder(context)
                                        .setTitle("提示")
                                        .setMessage("需要您开启定位权限")
                                        .setNegativeButton("取消", null)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                getAppDetailSettingIntent(context);
                                            }
                                        }).create().show();
                            }
                        } else {

                            if (isLocServiceEnable(context)) {
                                if (mLocationClient != null) {
                                    if (!mLocationClient.isStarted()) {
                                        mLocationClient.start();
                                        Log.d(TAG, "run: " + "开启定位");
                                    }
                                }
                            } else {
                                Toast.makeText(context, "请打开GPS开关", Toast.LENGTH_SHORT).show();
                            }

                        }

//                        context.startActivity(new Intent(context, FeedbackActivity.class));

                    }
                }
            });
        }

        @JavascriptInterface
        public String callAndroidMap() {
            Log.d(TAG, "run: " + "定位返回");
            return lat + "," + lng;
        }

        @JavascriptInterface
        public void callAndroidClose() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    if (context != null) {
//                        context.startActivity(new Intent(context, FeedbackActivity.class));
                        if (mLocationClient != null) {
                            Log.d(TAG, "run: " + "关闭定位");
                            mLocationClient.stop();
                        }
                    }
                }
            });
        }
    }

    public String getUrl() {
        return "";
    }

    protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        //AgentWeb 在触发某些敏感的 Action 时候会回调该方法， 比如定位触发 。
        //例如 https//:www.baidu.com 该 Url 需要定位权限， 返回false ，如果版本大于等于23 ， agentWeb 会动态申请权限 ，true 该Url对应页面请求定位失败。
        //该方法是每次都会优先触发的 ， 开发者可以做一些敏感权限拦截 。
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i("info", "url:" + url + "  permission:" + permissions + " action:" + action);
            return false;
        }
    };
    protected DownLoadResultListener mDownLoadResultListener = new DownLoadResultListener() {
        @Override
        public void success(String path) {

            Log.i("Info", "path-----------------------:" + path);
            /*File mFile=new File(path);
            mFile.delete();*/
        }

        @Override
        public void error(String path, String resUrl, String cause, Throwable e) {

            Log.i("Info", "path:" + path + "  url:" + resUrl + "  couse:" + cause + "  Throwable:" + e);
        }
    };

    public WebSettings getSettings() {
        return WebDefaultSettingsManager.getInstance();
    }

    protected ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {

        }
    };
    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };
    protected com.tencent.smtt.sdk.WebViewClient mWebViewClient = new com.tencent.smtt.sdk.WebViewClient() {
        private HashMap<String, Long> timer = new HashMap<>();

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }


        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            LogUtils.i("Info", "mWebViewClient shouldOverrideUrlLoading:" + url);
            //intent:// scheme的处理 如果返回false ， 则交给 DefaultWebClient 处理 ， 默认会打开该Activity  ， 如果Activity不存在则跳到应用市场上去.  true 表示拦截
            //例如优酷视频播放 ，intent://play?vid=XODEzMjU1MTI4&refer=&tuid=&ua=Mozilla%2F5.0%20(Linux%3B%20Android%207.0%3B%20SM-G9300%20Build%2FNRD90M%3B%20wv)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Version%2F4.0%20Chrome%2F58.0.3029.83%20Mobile%20Safari%2F537.36&source=exclusive-pageload&cookieid=14971464739049EJXvh|Z6i1re#Intent;scheme=youku;package=com.youku.phone;end;
            //优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
            if (url.startsWith("intent://"))
                return true;
            else if (url.startsWith("youku"))
                return true;
//            else if(isAlipay(view,url))  //不需要，defaultWebClient内部会自动处理
//                return true;


            return false;
        }

        int index = 1;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            Log.i(TAG, "onPageFinished  url:" + url + "  time:" + timer.get(url) + "   index:" + (index++));
            if (timer.get(url) != null) {

            }

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("Info", "onActivityResult -- >callback:" + requestCode + "   0x254:" + 0x254);
//        Log.i("Info","onActivityResult result");
        mAgentWebX5.uploadFileResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWebX5.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        mAgentWebX5.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mAgentWebX5.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAgentWebX5.getWebLifeCycle().onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onDestroy();
    }

    /**
     * 跳转到权限设置界面
     */
    private void getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public MiddleWareWebChromeBase getMiddleWareWebChrome() {
        return mMiddleWareWebChrome;
    }

    public MiddleWareWebClientBase getMiddleWareWebClient() {
        return mMiddleWareWebClient;
    }
}
