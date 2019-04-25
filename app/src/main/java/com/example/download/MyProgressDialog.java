package com.example.download;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * @创建者 李国赫
 * @创建时间 2019/4/24 14:52
 * @描述 水平progressbar的自定义
 */
public class MyProgressDialog extends AlertDialog {

    //显示title文本
    private TextView     titleProTv;
    //显示已下载percent文本
    private TextView     percentTv;
    //显示总量及已下载量文本
    private TextView     numTv;
    //progressbar
    private ProgressBar  mProgressBar;
    //handler
    private Handler      updateHandler;
    //设置最大值
    private int          mMax;
    //设置title
    private CharSequence titleProStr;
    //开始标志
    private boolean      startFlag = false;
    //当前下载量
    private int          currentPro;
    //总量及已下载量格式化
    private String       proNumberFormat;
    //当前下载量格式化
    private NumberFormat mNumberFormat;

    public MyProgressDialog(Context context) {
        super(context);
        initFormats();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_progress_dialog);
        initView();
        updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //获取progressbar最大值及下载量
                int progress = mProgressBar.getProgress();
                int max = mProgressBar.getMax();
                //总量及已下载量格式化
                if (proNumberFormat != null) {
                    String format = proNumberFormat;
                    numTv.setText(String.format(format,progress,max));
                } else {
                    numTv.setText("");
                }
                //当前下载量格式化
                if (mNumberFormat != null) {
                    double percent = (double) progress / (double) max;
                    SpannableString tmp = new SpannableString(mNumberFormat.format(percent));
                    percentTv.setText(tmp);
                } else {
                    percentTv.setText("");
                }
            }
        };
        //设置title
        if (titleProStr != null) {
            setMessage(titleProStr);
        }
        //设置最大值
        if (mMax > 0) {
            setMax(mMax);
        }
        //设置当前下载量
        if (currentPro > 0) {
            setProgress(currentPro);
        }
        //正在下载
        onProgressChanged();

    }

    public void initView() {
        titleProTv = (TextView) findViewById(R.id.progress_title);
        percentTv = (TextView) findViewById(R.id.progress_percent);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        numTv = (TextView) findViewById(R.id.progress_number);
    }

    private void initFormats() {
        //1代表下载量值
        // 2代表总量值
        // .2代表2位小数
        proNumberFormat = "%1d/%2d";
        //下载量百分比显示
        mNumberFormat = NumberFormat.getPercentInstance();
        //0代表不显示小数位
        mNumberFormat.setMaximumFractionDigits(0);
    }

    //设置最大值
    public void setMax(int max) {
        if (mProgressBar != null) {
            mProgressBar.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    //设置当前进程
    public void setProgress(int value) {
        if (startFlag) {
            mProgressBar.setProgress(value);
            onProgressChanged();
        } else {
            currentPro = value;
        }
    }

    //设置显示文本
    @Override
    public void setMessage(CharSequence message) {

        if (titleProTv != null) {
            titleProTv.setText(message);
        } else {
            titleProStr = message;
        }
    }

    private void onProgressChanged() {
        updateHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startFlag = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        startFlag = false;
    }
}
