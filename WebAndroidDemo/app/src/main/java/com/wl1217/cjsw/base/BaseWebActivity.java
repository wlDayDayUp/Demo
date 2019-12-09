//package com.wl1217.cysw.base;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.net.http.SslError;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.webkit.GeolocationPermissions;
//import android.webkit.SslErrorHandler;
//import android.webkit.ValueCallback;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//
//import com.just.agentweb.AbsAgentWebSettings;
//import com.just.agentweb.AgentWeb;
//import com.just.agentweb.IAgentWebSettings;
//import com.just.agentweb.PermissionInterceptor;
//import com.just.agentweb.WebChromeClient;
//import com.just.agentweb.WebViewClient;
//import com.wl1217.cysw.BuildConfig;
//import com.wl1217.cysw.R;
//
//import java.io.File;
//import java.util.UUID;
//
///**
// * Created by helloworld on 2018/1/3.
// */
//
//public class BaseWebActivity extends AppCompatActivity {
//
//    protected AgentWeb mAgentWeb;
//    private LinearLayout mLinearLayout;
//    private LinearLayout layout_menu_back;
//    private TextView mTitleTextView;
//    private static final int REQUEST_PERMISSION = 0;
//    private static final int REQUEST_CODE_ALBUM = 0x01;
//    private static final int REQUEST_CODE_CAMERA = 0x02;
//    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0x03;
//    private ValueCallback<Uri[]> uploadMessageAboveL;
//    private ValueCallback<Uri> uploadMessage;
//    private String mLastPhothPath;
//    private Thread mThread;
//    private String mCurrentPhotoPath;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_web);
//
//        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
//        this.findViewById(R.id.layout_menu_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BaseWebActivity.this.finish();
//            }
//        });
//
//        mTitleTextView = ((TextView) this.findViewById(R.id.top_title_tv));
//        layout_menu_back = this.findViewById(R.id.layout_menu_back);
//
//        layout_menu_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!mAgentWeb.back())// true表示AgentWeb处理了该事件
//                    BaseWebActivity.this.finish();
//            }
//        });
//
//        mAgentWeb = AgentWeb.with(this)
//                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
//                .closeIndicator()
//                .setAgentWebWebSettings(getSettings())//设置 AgentWebSettings。
//                .setWebViewClient(mWebViewClient)//WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
//                .setWebChromeClient(mWebChromeClient)
//                .setPermissionInterceptor(mPermissionInterceptor) //权限拦截 2.0.0 加入。
//                .setMainFrameErrorView(R.layout.custom_error_view, -1)
//                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) // (AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
//                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
////                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
//                .createAgentWeb()
//                .ready()
//                .go(getUrl());
//
//
//        mSetting();
//
//
//        // 权限-----------
//        PackageManager pkgManager = getPackageManager();
//
//        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
//        boolean sdCardWritePermission =
//                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // 读sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
//        boolean sdCardReadPermission =
//                pkgManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // read phone state用于获取 imei 设备信息
//        boolean phoneSatePermission =
//                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // 相机权限
//        boolean cramerSatePermission =
//                pkgManager.checkPermission(Manifest.permission.CAMERA, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // 地理位置权限
//        boolean coarseLocationPermission =
//                pkgManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // 地理位置权限
//        boolean accessFineLocationPermission =
//                pkgManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        if (Build.VERSION.SDK_INT >= 23
//                && !sdCardWritePermission ||
//                !phoneSatePermission ||
//                !cramerSatePermission ||
//                !coarseLocationPermission ||
//                !accessFineLocationPermission
//        ) {
//            requestPermission();
//        }
//    }
//
//    // ------------------------------------------------------------
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                },
//                REQUEST_PERMISSION);
//    }
//
//    private void mSetting() {
//        WebView webView = mAgentWeb.getWebCreator().getWebView();
//
//        webView.getSettings().setAllowFileAccess(false);
//        webView.getSettings().setSavePassword(false);
//        webView.removeJavascriptInterface("searchBoxJavaBridge_");
//        webView.removeJavascriptInterface("accessibility");
//        webView.removeJavascriptInterface("accessibilityTraversal");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        }
//        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setAppCachePath(getCacheDir().getPath());
//        webView.getSettings().setDomStorageEnabled(true);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            webView.setWebContentsDebuggingEnabled(true);
////        }
////        if (mAgentWeb != null) {
////            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, this));
////        }
//    }
//
////    public class AndroidInterface {
////        private Handler deliver = new Handler(Looper.getMainLooper());
////        private AgentWeb agent;
////        private Context context;
////
////        public AndroidInterface(AgentWeb agent, Context context) {
////            this.agent = agent;
////            this.context = context;
////        }
////
////
//////        @JavascriptInterface
//////        public void callAndroidGoFk() {
//////
//////            deliver.post(new Runnable() {
//////                @Override
//////                public void run() {
//////                    if (context != null) {
//////                        context.startActivity(new Intent(context, FeedbackActivity.class));
//////
//////                    }
//////                }
//////            });
//////        }
////
////        @JavascriptInterface
////        public void callAndroidBright() {
////
////            deliver.post(new Runnable() {
////                @Override
////                public void run() {
////                    if (context != null) {
////                        Log.d("wg", "run: -------亮--------------");
////                        Window window = getWindow();
////                        WindowManager.LayoutParams lp = window.getAttributes();
////                        lp.screenBrightness = 1;
////                        window.setAttributes(lp);
////                    }
////                }
////            });
////        }
////
////        @JavascriptInterface
////        public void callAndroidBrightLow() {
////
////            deliver.post(new Runnable() {
////                @Override
////                public void run() {
////                    if (context != null) {
////                        Log.d("wg", "run: ---------低------------");
////                        Window window = getWindow();
////                        WindowManager.LayoutParams lp = window.getAttributes();
////                        lp.screenBrightness = -1;
////                        window.setAttributes(lp);
////                    }
////                }
////            });
////        }
////    }
//
//
//    private WebViewClient mWebViewClient = new WebViewClient() {
//
//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            handler.proceed();// 接受所有网站的证书
////            handler.cancel();
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            return super.shouldOverrideUrlLoading(view, request);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//        }
//
//    };
//
//
//    protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {
//
//        //AgentWeb 在触发某些敏感的 Action 时候会回调该方法， 比如定位触发 。
//        //例如 https//:www.baidu.com 该 Url 需要定位权限， 返回false ，如果版本大于等于23 ， agentWeb 会动态申请权限 ，true 该Url对应页面请求定位失败。
//        //该方法是每次都会优先触发的 ， 开发者可以做一些敏感权限拦截 。
//        @Override
//        public boolean intercept(String url, String[] permissions, String action) {
//            return false;
//        }
//    };
//
//
//    public String getUrl() {
//        return "";
//    }
//
//    private String geoRequestOrigin = "";
//    private GeolocationPermissions.Callback geoRequestCallback;
//
//    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
//
//        //For Android  >= 5.0
//        @Override
//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//
//            uploadMessageAboveL = filePathCallback;
//            uploadPicture();
//            return true;
//        }
//
//
//        //For Android  >= 4.1
//        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
//            uploadMessage = valueCallback;
//            uploadPicture();
//        }
//
//
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//        }
//
//        @Override
//        public void onReceivedTitle(WebView view, String title) {
//            super.onReceivedTitle(view, title);
//            if (mTitleTextView != null && !TextUtils.isEmpty(title))
//                if (title.length() > 10)
//                    title = title.substring(0, 10).concat("...");
//            mTitleTextView.setText(title);
//        }
//
//        @Override
//        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//
//            geoRequestOrigin = origin;
//            geoRequestCallback = callback;
//
//            if (ContextCompat.checkSelfPermission(BaseWebActivity.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                        BaseWebActivity.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        101
//                );
//            } else {
//                callback.invoke(origin, true, true);
//            }
//
//
////            callback.invoke(origin, true, false);
////            super.onGeolocationPermissionsShowPrompt(origin, callback);
//        }
//    };
//
//    /**
//     * 选择相机或者相册
//     */
//    public void uploadPicture() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("请选择图片上传方式");
//
//        //取消对话框
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                //一定要返回null,否则<input type='file'>
//                if (uploadMessage != null) {
//                    uploadMessage.onReceiveValue(null);
//                    uploadMessage = null;
//                }
//                if (uploadMessageAboveL != null) {
//                    uploadMessageAboveL.onReceiveValue(null);
//                    uploadMessageAboveL = null;
//
//                }
//            }
//        });
//
//        builder.setItems(new String[]{"相机拍照", "相册选择"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d("wg", "onClick: ------------ " + i);
//
//                if (i == 0) {
//                    if (!TextUtils.isEmpty(mLastPhothPath)) {
////                    //上一张拍照的图片删除
//                        mThread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                File file = new File(mLastPhothPath);
//                                if (file != null) {
//                                    file.delete();
//                                }
//                                mHandler.sendEmptyMessage(1);
//                            }
//                        });
//                        mThread.start();
//                    } else {
//                        //请求拍照权限
//                        if (ActivityCompat.checkSelfPermission(BaseWebActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                            takePhoto();
//                        } else {
//                            ActivityCompat.requestPermissions(BaseWebActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
//                        }
//                    }
//                } else if (i == 1) {
//                    chooseAlbumPic();
//                }
//            }
//        });
//
////        builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////
////
////                if (!TextUtils.isEmpty(mLastPhothPath)) {
////                    //上一张拍照的图片删除
////                    mThread = new Thread(new Runnable() {
////                        @Override
////                        public void run() {
////
////                            File file = new File(mLastPhothPath);
////                            if (file != null) {
////                                file.delete();
////                            }
////                            mHandler.sendEmptyMessage(1);
////
////                        }
////                    });
////
////                    mThread.start();
////
////
////                } else {
////
////                    //请求拍照权限
////                    if (ActivityCompat.checkSelfPermission(BaseWebActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
////                        takePhoto();
////                    } else {
////                        ActivityCompat.requestPermissions(BaseWebActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
////                    }
////                }
////
////
////            }
////        });
////        builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////
////                chooseAlbumPic();
////
////
////            }
////        });
//
//        builder.create().
//
//                show();
//
//    }
//
//    /**
//     * 拍照
//     */
//    private void takePhoto() {
//
//        StringBuilder fileName = new StringBuilder();
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileName.append(UUID.randomUUID()).append("_upload.png");
//        File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName.toString());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        } else {
//            Uri uri = Uri.fromFile(tempFile);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        }
//
//        mCurrentPhotoPath = tempFile.getAbsolutePath();
//        startActivityForResult(intent, REQUEST_CODE_CAMERA);
//
//
//    }
//
//    /**
//     * 选择相册照片
//     */
//    private void chooseAlbumPic() {
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == 101) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (geoRequestCallback != null) {
//                    geoRequestCallback.invoke(geoRequestOrigin, true, true);
//                }
//            } else {
//                if (geoRequestCallback != null) {
//                    geoRequestCallback.invoke(geoRequestOrigin, false, false);
//                }
//            }
//        }
//    }
//
//    public IAgentWebSettings getSettings() {
//        return new AbsAgentWebSettings() {
//            private AgentWeb mAgentWeb;
//
//            @Override
//            protected void bindAgentWebSupport(AgentWeb agentWeb) {
//                this.mAgentWeb = agentWeb;
//            }
//        };
//    }
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onPause() {
//        mAgentWeb.getWebLifeCycle().onPause();
//        super.onPause();
//
//    }
//
//    @Override
//    protected void onResume() {
//        mAgentWeb.getWebLifeCycle().onResume();
//        super.onResume();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_CAMERA) {
//
//            if (uploadMessage == null && uploadMessageAboveL == null) {
//                return;
//            }
//
//            //取消拍照或者图片选择时
//            if (resultCode != RESULT_OK) {
//                //一定要返回null,否则<input file> 就是没有反应
//                if (uploadMessage != null) {
//                    uploadMessage.onReceiveValue(null);
//                    uploadMessage = null;
//                }
//                if (uploadMessageAboveL != null) {
//                    uploadMessageAboveL.onReceiveValue(null);
//                    uploadMessageAboveL = null;
//
//                }
//            }
//
//            //拍照成功和选取照片时
//            if (resultCode == RESULT_OK) {
//                Uri imageUri = null;
//
//                switch (requestCode) {
//                    case REQUEST_CODE_ALBUM:
//
//                        if (data != null) {
//                            imageUri = data.getData();
//                        }
//
//                        break;
//                    case REQUEST_CODE_CAMERA:
//
//                        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
//                            File file = new File(mCurrentPhotoPath);
//                            Uri localUri = Uri.fromFile(file);
//                            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
//                            sendBroadcast(localIntent);
//                            imageUri = Uri.fromFile(file);
//                            mLastPhothPath = mCurrentPhotoPath;
//                        }
//                        break;
//                }
//
//
//                //上传文件
//                if (uploadMessage != null) {
//                    uploadMessage.onReceiveValue(imageUri);
//                    uploadMessage = null;
//                }
//                if (uploadMessageAboveL != null) {
//                    uploadMessageAboveL.onReceiveValue(new Uri[]{imageUri});
//                    uploadMessageAboveL = null;
//
//                }
//
//            }
//
//        }
//
//
//    }
//
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            takePhoto();
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mAgentWeb.getWebLifeCycle().onDestroy();
//        mThread = null;
//        mHandler = null;
//    }
//}
