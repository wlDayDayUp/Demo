package com.wl1217.cjsw;


import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;

import com.rxjava.rxlife.RxLife;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import rxhttp.wrapper.param.RxHttp;


public class MainActivity extends BaseWebActivity {

    private String[] NEED_PERMISSION = {

    };

    private final int PERMISSION_CODE = 123;
    private Disposable checkDisposable;
    private Disposable downApkDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            int request = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int request1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int request2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//            if (request != PackageManager.PERMISSION_GRANTED || request1 != PackageManager.PERMISSION_GRANTED || request2 != PackageManager.PERMISSION_GRANTED)
//            {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            }, 123);
//            }
        }

        checkAndUpdate();
    }

    @Override
    public String getUrl() {
        return "http://114.220.179.71:81/"; //长江水务
//        return "https://h5.m.jd.com/active/download/download.html?channel=jd-msy1"; //长江水务
//        return "https://map.baidu.com/"; //长江水务
//        return "http://192.168.0.137:8080/cyjycl/yzjy/index/index2.htm";
//        return "http://1l865v9452.iask.in:8889/yzjsj/weixin_cs/index.html"; //长江水务
//        return "http://222.189.189.206/jsxyapp/baodao/index.html#/";
//        return "http://192.168.0.105:8080/#/";
    }


    private ProgressDialog mProgressDialog; /*进度框*/


    public void checkAndUpdate() {
        // 调接口检查，判断VersionCode
        checkDisposable = RxHttp.get(Url.updateVserion)
                .asObject(VersionUpdate.class)
                .subscribe(versionUpdate -> {
                    try {
                        // 本地版本号
                        int localVersionCode = getPackageManager()
                                .getPackageInfo(getPackageName(), 0).versionCode;
                        // 服务器版本号
                        int remoteVersionCode = Integer.parseInt(versionUpdate.getCode());
                        if (remoteVersionCode > localVersionCode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showUserUpdateDialog(versionUpdate.getMsg(), new IDialogBtCallback() {
                                        @Override
                                        public void confirm() {
                                            downApk();
                                        }

                                        @Override
                                        public void cancel() {
                                            exit(); /*强制更新*/
                                        }
                                    });
                                }
                            });
                            // 下载更新

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
    }

    /**
     * 下载更新
     */
    private void downApk() {
        showDownApkProgressDialog(); // 显示下载进度条
        final String fileName = "/" + Url.APP_NAME;
        String fileStoreDir = getCacheDir().getPath(); // 不需要动态获取权限/data/user/0/com.daydayup.mecookies/cache
        downApkDisposable = RxHttp.get(Url.downApk)
                .asDownload(fileStoreDir + fileName, progress -> {
                    //下载进度回调,0-100，仅在进度有更新时才会回调，最多回调101次，最后一次回调文件存储路径
                    int currentProgress = progress.getProgress(); //当前进度 0-100
//                    long currentSize = progress.getCurrentSize(); //当前已下载的字节大小
//                    long totalSize = progress.getTotalSize();     //要下载的总字节大小
                    updateProgress(currentProgress); // 更新进度条
                }, AndroidSchedulers.mainThread())
                .subscribe(s -> { // s 文件保存的路径
                    missDownApkProgressDialog(); // 关闭进度条
                    installApk(s); // 安装APK
                }, throwable -> {
                    throwable.printStackTrace();
                    missDownApkProgressDialog(); // 关闭进度条
                });
    }

    /**
     * 安装APK
     *
     * @param apkPath 下载后apk保存的全路径
     */
    private void installApk(String apkPath) {

        String command = "chmod 777 " + apkPath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command); /* 设置文件执行权限*/
            // 发起系统安装apk的intent
            File file = new File(apkPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
                Uri apkUri = FileProvider.getUriForFile(this, "com.wl1217.cjsw.fileProvider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束程序
     */
    private void exit() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 显示下载进度条dialog
     */
    private void showDownApkProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("重要更新");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
    }

    /**
     * 关闭下载进度条dialog
     */
    private void missDownApkProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 更新进度条
     *
     * @param progress 下载进度
     */
    private void updateProgress(int progress) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(progress);
        }
    }

    private void showUserUpdateDialog(String msg, IDialogBtCallback iDialogBtCallback) {
        AlertDialog mAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(Html.fromHtml(msg).toString())
                .setPositiveButton("更新", (dialogInterface, i) -> iDialogBtCallback.confirm())
                .setNegativeButton("取消", (dialogInterface, i) -> iDialogBtCallback.cancel())
                .create();
        mAlertDialog.show();
        mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }

    @Override
    protected void onDestroy() {

        if (checkDisposable != null) {
            if (!checkDisposable.isDisposed()) {
                checkDisposable.dispose();
            }
        }
        if (downApkDisposable != null) {
            if (!downApkDisposable.isDisposed()) {
                downApkDisposable.dispose();
            }
        }
        super.onDestroy();
    }
}
