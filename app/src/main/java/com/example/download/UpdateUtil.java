package com.example.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @创建者 李国赫
 * @创建时间 2019/4/23 10:25
 * @描述 app自动更新，后台下载，断点下载选择
 * 需提供：
 * 1、下载地址URL
 * 2、apk需要保存位置
 * 3、apk名称
 * 4、新版本号
 */
public class UpdateUtil {

    private static final int DOWNLOADING     = 1;
    private static final int DOWNLOAD_FINISH = 2;

    private Activity         activity;
    private String           packageName;
    private int              newVersionCode;
    private String           downloadUrl;
    private String           apkName;
    private String           savePath;
    private ProgressDialog   mProgressDialog;
    private MyDialog         mMyDialog;
    private MyProgressDialog mMyProgressDialog;
    private int              progress   = 0;
    //下载完成标志
    private boolean          updateFlag = false;

    public UpdateUtil(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOADING:
                    mMyProgressDialog.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
            }
        }
    };

    /**
     * 获取当前版本号
     */
    private int getVersioninfo() {
        PackageManager pm = activity.getPackageManager();
        try {
            //0代表所有信息提取到PackageInfo中
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);
            packageName = pi.packageName;
            return pi.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 联网查询版本号,有新版本更新
     */
    public void requestVersionCode(final int newCode, final String path, final String name, final String url) {
        new Thread() {
            @Override
            public void run() {
                newVersionCode = newCode;
                savePath = path;
                apkName = name;
                downloadUrl = url;
                //获取当前版本号
                int curVersuinCode = getVersioninfo();
                if (curVersuinCode == Integer.MAX_VALUE) {
                    return;
                } else {
                    if (newVersionCode > curVersuinCode) {
                        //启动下载
                        showUpdateProgress();
                    } else {
                        return;
                    }
                }


            }

        }.start();

    }

    /**
     * 下载更新，不更新直接退出程序
     */
    private void showUpdateProgress() {
        //开启线程更新
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新对话框
                mMyDialog = new MyDialog(activity);
                mMyDialog.setTitle("升级提示");
                mMyDialog.setMessage("有新版本，请下载更新");
                mMyDialog.setCancelable(false);
                mMyDialog.setCancelOnClickListener("退出应用",
                        new MyDialog.CancelOnClickListener() {
                            @Override
                            public void onCancelClick() {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                mMyDialog.setConfirmOnClickListener("立即更新",
                        new MyDialog.ConfirmOnClickListener() {
                            @Override
                            public void onConfirmClick() {
                                mMyProgressDialog = new MyProgressDialog(activity);
                                mMyProgressDialog.setMax(100);
                                mMyProgressDialog.setTitle("正在下载...");
                                mMyProgressDialog.setCancelable(false);
                                mMyDialog.dismiss();
                                mMyProgressDialog.show();
                                //判断文件读写权限
                                if (ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    mMyProgressDialog.dismiss();
                                    ActivityCompat.requestPermissions
                                            (activity, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                                } else {
                                    new DownloadApk().start();
                                }
                            }
                        });
                mMyDialog.show();
            }
        });
    }

    /**
     * 下载
     */
    private class DownloadApk extends Thread {
        @Override
        public void run() {
            try {
                //判断sd卡状态
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //传入下载路径
                    URL url = new URL(downloadUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int length = connection.getContentLength();
                    InputStream is = connection.getInputStream();

                    File file = new File(savePath);
                    if (file.exists()) {
                        file.mkdir();
                    }
                    //获取apk名，保持一致
                    File apkFile = new File(savePath, apkName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte[] buf = new byte[1024];
                    do {
                        int num = is.read(buf);
                        count += num;
                        //计算下载进度
                        progress = (int) ((float) count / length * 100);
                        mHandler.sendEmptyMessage(DOWNLOADING);
                        //进度传递
                        if (num <= 0) {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            updateFlag = true;
                            mMyProgressDialog.dismiss();
                            break;
                        }
                        fos.write(buf, 0, num);
                    } while (!updateFlag);
                    //关闭资源
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 支持断点下载
     */


    /**
     * 安装apk
     */
    private void installApk() {
        File newFile = new File(savePath, apkName);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //Android7.0后自动更新需要通过FileProvider读取文件和完成更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(activity,
                    packageName + ".FileProvider", newFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "applacation/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(newFile), "applacation/vnd.android.package-archive");
        }
        activity.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
